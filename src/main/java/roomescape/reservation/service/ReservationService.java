package roomescape.reservation.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.domain.AuthInfo;
import roomescape.exception.custom.BadRequestException;
import roomescape.exception.custom.ForbiddenException;
import roomescape.global.restclient.PaymentWithRestClient;
import roomescape.member.domain.Member;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.reservation.controller.dto.PaymentRequest;
import roomescape.reservation.controller.dto.ReservationPaymentRequest;
import roomescape.reservation.controller.dto.ReservationQueryRequest;
import roomescape.reservation.controller.dto.ReservationRequest;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.controller.dto.ReservationWithStatus;
import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationSlot;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.repository.PaymentRepository;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationSlotRepository;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.reservation.domain.repository.ThemeRepository;
import roomescape.reservation.domain.specification.ReservationSpecification;

@Service
@Transactional
public class ReservationService {

    private final MemberRepository memberRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final ReservationSlotRepository reservationSlotRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentWithRestClient paymentWithRestClient;
    private final PaymentRepository paymentRepository;

    public ReservationService(MemberRepository memberRepository,
                              ReservationTimeRepository reservationTimeRepository,
                              ThemeRepository themeRepository,
                              ReservationSlotRepository reservationSlotRepository,
                              ReservationRepository reservationRepository,
                              PaymentWithRestClient paymentWithRestClient, PaymentRepository paymentRepository) {
        this.memberRepository = memberRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.reservationSlotRepository = reservationSlotRepository;
        this.reservationRepository = reservationRepository;
        this.paymentWithRestClient = paymentWithRestClient;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findReservations(ReservationQueryRequest request) {
        Specification<Reservation> spec = Specification
                .where(ReservationSpecification.greaterThanOrEqualToStartDate(request.getStartDate()))
                .and(ReservationSpecification.lessThanOrEqualToEndDate(request.getEndDate()))
                .and(ReservationSpecification.equalMemberId(request.getMemberId()))
                .and(ReservationSpecification.equalThemeId(request.getThemeId()));
        return reservationRepository.findAll(spec)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationWithStatus> findReservations(AuthInfo authInfo) {
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(() -> new BadRequestException("해당 유저를 찾을 수 없습니다."));
        return reservationRepository.findAllByMember(member).stream()
                .map(reservation -> ReservationWithStatus.of(reservation,
                        findPaymentById(reservation.getPayment().getId())))
                .toList();
    }

    private Payment findPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("해당 ID에 대응되는 결제 정보가 없습니다."));
    }

    public ReservationResponse createReservation(ReservationPaymentRequest reservationPaymentRequest, Long memberId) {
        PaymentRequest paymentRequest = new PaymentRequest(reservationPaymentRequest);
        Payment payment = paymentWithRestClient.confirm(paymentRequest);
        paymentRepository.save(payment);

        ReservationRequest reservationRequest = new ReservationRequest(reservationPaymentRequest.date(),
                reservationPaymentRequest.timeId(), reservationPaymentRequest.themeId());
        Reservation reservation = reservationRepository.save(findReservation(reservationRequest, memberId, payment));

        return ReservationResponse.from(reservation);
    }

    public ReservationResponse createAdminReservation(ReservationRequest reservationRequest, Long memberId) {
        ReservationSlot reservationSlot = findReservationSlot(reservationRequest);
        Member member = findMember(memberId);
        ReservationStatus reservationStatus = findReservationStatus(reservationSlot);
        Payment adminPayment = new Payment("adminPaymentKey",
                "adminOrderId",
                "adminOrderName",
                "adminMethod",
                1000L,
                "adminStatus",
                "adminRequestedAt",
                "adminApprovedAt");
        paymentRepository.save(adminPayment);

        validateReservation(reservationSlot, member);

        Reservation reservation = new Reservation(member, reservationSlot, reservationStatus, adminPayment);

        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    private Reservation findReservation(ReservationRequest reservationRequest, Long memberId, Payment payment) {
        ReservationSlot reservationSlot = findReservationSlot(reservationRequest);
        Member member = findMember(memberId);
        ReservationStatus reservationStatus = findReservationStatus(reservationSlot);

        validateReservation(reservationSlot, member);

        return new Reservation(member, reservationSlot, reservationStatus, payment);
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException("해당 유저를 찾을 수 없습니다."));
    }

    private ReservationSlot findReservationSlot(ReservationRequest reservationRequest) {
        LocalDate date = LocalDate.parse(reservationRequest.date());
        ReservationTime reservationTime = reservationTimeRepository.findById(reservationRequest.timeId())
                .orElseThrow(() -> new BadRequestException("해당 ID에 대응되는 예약 시간이 없습니다."));
        Theme theme = themeRepository.findById(reservationRequest.themeId())
                .orElseThrow(() -> new BadRequestException("해당 ID에 대응되는 테마가 없습니다."));

        return reservationSlotRepository.findFirstByDateAndTimeAndTheme(date, reservationTime, theme)
                .orElseGet(() -> reservationSlotRepository.save(new ReservationSlot(date, reservationTime, theme)));
    }

    private ReservationStatus findReservationStatus(ReservationSlot reservationSlot) {
        if (reservationRepository.existsByReservationSlot(reservationSlot)) {
            return ReservationStatus.WAITING;
        }
        return ReservationStatus.BOOKED;
    }

    private void validateReservation(ReservationSlot reservationSlot, Member member) {
        if (reservationSlot.isPast()) {
            throw new BadRequestException("올바르지 않는 데이터 요청입니다.");
        }
        if (reservationRepository.existsByReservationSlotAndMember(reservationSlot, member)) {
            throw new ForbiddenException("중복된 예약입니다.");
        }
    }

    public void deleteReservation(AuthInfo authInfo, long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BadRequestException("해당 ID에 대응되는 사용자 예약이 없습니다."));
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(() -> new BadRequestException("해당 유저를 찾을 수 없습니다."));
        if (!member.isAdmin() && !reservation.isBookedBy(member)) {
            throw new ForbiddenException("예약자가 아닙니다.");
        }
        reservationRepository.deleteById(reservationId);
    }

    public void delete(long reservationId) {
        reservationRepository.deleteByReservationSlot_Id(reservationId);
        reservationSlotRepository.deleteById(reservationId);
    }
}
