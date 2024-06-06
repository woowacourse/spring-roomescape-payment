package roomescape.reservation.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.domain.AuthInfo;
import roomescape.exception.custom.BadRequestException;
import roomescape.exception.custom.ForbiddenException;
import roomescape.member.domain.Member;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.payment.application.PaymentService;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.PaymentRequest;
import roomescape.reservation.controller.dto.*;
import roomescape.reservation.domain.*;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationSlotRepository;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.reservation.domain.repository.ThemeRepository;
import roomescape.reservation.domain.specification.ReservationSpecification;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    private final MemberRepository memberRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final ReservationSlotRepository reservationSlotRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentService paymentService;

    public ReservationService(MemberRepository memberRepository,
                              ReservationTimeRepository reservationTimeRepository,
                              ThemeRepository themeRepository,
                              ReservationSlotRepository reservationSlotRepository,
                              ReservationRepository reservationRepository, PaymentService paymentService) {
        this.memberRepository = memberRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.reservationSlotRepository = reservationSlotRepository;
        this.reservationRepository = reservationRepository;
        this.paymentService = paymentService;
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
        return reservationRepository.findAllByMember(member)
                .stream()
                .map(ReservationWithStatus::from)
                .toList();
    }

    @Transactional
    public ReservationResponse reserve(ReservationPaymentRequest reservationPaymentRequest, Long memberId) {
        ReservationRequest reservationRequest = new ReservationRequest(reservationPaymentRequest.date(), reservationPaymentRequest.timeId(), reservationPaymentRequest.themeId());

        if (reservationRepository.existsByDateAndTimeIdAndThemeId(
                LocalDate.parse(reservationRequest.date()),
                reservationRequest.themeId(),
                reservationRequest.timeId())
        ) {
            throw new BadRequestException("이미 예약된 상태입니다. 예약 대기를 진행하거나 다른 예약을 선택해주세요.");
        }
        PaymentRequest paymentRequest = new PaymentRequest(reservationPaymentRequest);
        Payment payment = paymentService.purchase(paymentRequest, reservationPaymentRequest.amount());
        return createReservationWithPayment(reservationRequest, memberId, ReservationStatus.BOOKED, payment);
    }

    @Transactional
    public ReservationResponse createReservationWithPayment(
            ReservationRequest reservationRequest, Long memberId, ReservationStatus reservationStatus, Payment payment
    ) {
        LocalDate date = LocalDate.parse(reservationRequest.date());
        ReservationTime reservationTime = reservationTimeRepository.findById(reservationRequest.timeId())
                .orElseThrow(() -> new BadRequestException("해당 ID에 대응되는 예약 시간이 없습니다."));
        Theme theme = themeRepository.findById(reservationRequest.themeId())
                .orElseThrow(() -> new BadRequestException("해당 ID에 대응되는 테마가 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException("해당 유저를 찾을 수 없습니다."));
        ReservationSlot reservationSlot = reservationSlotRepository.findByDateAndTimeAndTheme(date, reservationTime, theme)
                .orElseGet(() -> reservationSlotRepository.save(new ReservationSlot(date, reservationTime, theme)));

        validateReservation(reservationSlot, member);

        Reservation reservation = reservationRepository.save(
                new Reservation(member, reservationSlot, reservationStatus, payment));
        return ReservationResponse.from(reservation.getId(), reservationSlot, member);
    }

    @Transactional
    public ReservationResponse createReservation(
            ReservationRequest reservationRequest, Long memberId, ReservationStatus reservationStatus
    ) {
        LocalDate date = LocalDate.parse(reservationRequest.date());
        ReservationTime reservationTime = reservationTimeRepository.findById(reservationRequest.timeId())
                .orElseThrow(() -> new BadRequestException("해당 ID에 대응되는 예약 시간이 없습니다."));
        Theme theme = themeRepository.findById(reservationRequest.themeId())
                .orElseThrow(() -> new BadRequestException("해당 ID에 대응되는 테마가 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException("해당 유저를 찾을 수 없습니다."));
        ReservationSlot reservationSlot = reservationSlotRepository.findByDateAndTimeAndTheme(date, reservationTime, theme)
                .orElseGet(() -> reservationSlotRepository.save(new ReservationSlot(date, reservationTime, theme)));

        validateReservation(reservationSlot, member);

        Reservation reservation = reservationRepository.save(
                new Reservation(member, reservationSlot, reservationStatus));
        return ReservationResponse.from(reservation.getId(), reservationSlot, member);
    }

    private void validateReservation(ReservationSlot reservationSlot, Member member) {
        if (reservationSlot.isPast()) {
            throw new BadRequestException("올바르지 않는 데이터 요청입니다.");
        }
        if (reservationRepository.existsByReservationSlotAndMember(reservationSlot, member)) {
            throw new ForbiddenException("중복된 예약입니다.");
        }
    }

    @Transactional
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

    @Transactional
    public void delete(long reservationId) {
        reservationRepository.deleteByReservationSlot_Id(reservationId);
        reservationSlotRepository.deleteById(reservationId);
    }
}
