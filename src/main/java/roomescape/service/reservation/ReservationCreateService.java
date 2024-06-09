package roomescape.service.reservation;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.dto.PaymentRequest;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.reservationdetail.ReservationDetailRepository;
import roomescape.domain.schedule.ReservationDate;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.schedule.ReservationTimeRepository;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.exception.ForbiddenException;
import roomescape.exception.InvalidMemberException;
import roomescape.exception.InvalidReservationException;
import roomescape.service.payment.PaymentService;
import roomescape.service.reservation.dto.AdminReservationRequest;
import roomescape.service.reservation.dto.ReservationRequest;
import roomescape.service.reservation.dto.ReservationResponse;
import roomescape.service.reservation.dto.WaitingPaymentRequest;

@Service
@Transactional
public class ReservationCreateService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final ReservationDetailRepository reservationDetailRepository;
    private final PaymentService paymentService;

    public ReservationCreateService(ReservationRepository reservationRepository,
        ReservationTimeRepository reservationTimeRepository, ThemeRepository themeRepository,
        MemberRepository memberRepository, ReservationDetailRepository reservationDetailRepository, PaymentService paymentService) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.reservationDetailRepository = reservationDetailRepository;
        this.paymentService = paymentService;
    }

    public ReservationResponse createAdminReservation(AdminReservationRequest adminReservationRequest) {
        Reservation reservation = createReservation(adminReservationRequest.timeId(), adminReservationRequest.themeId(),
            adminReservationRequest.memberId(), adminReservationRequest.date());

        return new ReservationResponse(reservation);
    }

    public ReservationResponse createMemberReservation(ReservationRequest reservationRequest, long memberId) {
        Reservation reservation = createReservation(reservationRequest.timeId(), reservationRequest.themeId(), memberId,
            reservationRequest.date());

        PaymentRequest request = new PaymentRequest(reservationRequest.paymentKey(), reservationRequest.orderId(), reservationRequest.amount());
        paymentService.approvePayment(request, reservation);

        return new ReservationResponse(reservation);
    }

    private Reservation createReservation(long timeId, long themeId, long memberId, LocalDate date) {
        ReservationDate reservationDate = ReservationDate.of(date);
        ReservationTime reservationTime = findTimeById(timeId);
        Theme theme = findThemeById(themeId);
        Member member = findMemberById(memberId);
        ReservationDetail reservationDetail = getReservationDetail(reservationDate, reservationTime, theme);
        validateDuplication(reservationDetail);

        return reservationRepository.save(new Reservation(member, reservationDetail, ReservationStatus.RESERVED));
    }

    private ReservationTime findTimeById(long timeId) {
        return reservationTimeRepository.findById(timeId)
            .orElseThrow(() -> new InvalidReservationException("더이상 존재하지 않는 시간입니다."));
    }

    private Theme findThemeById(long themeId) {
        return themeRepository.findById(themeId)
            .orElseThrow(() -> new InvalidReservationException("더이상 존재하지 않는 테마입니다."));
    }

    private Member findMemberById(long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new InvalidMemberException("존재하지 않는 회원입니다."));
    }

    private ReservationDetail getReservationDetail(ReservationDate reservationDate, ReservationTime reservationTime, Theme theme) {
        Schedule schedule = new Schedule(reservationDate, reservationTime);
        return reservationDetailRepository.findByScheduleAndTheme(schedule, theme)
            .orElseGet(() -> reservationDetailRepository.save(new ReservationDetail(schedule, theme)));
    }

    private void validateDuplication(ReservationDetail reservationDetail) {
        if (reservationRepository.existsByDetailId(reservationDetail.getId())) {
            throw new InvalidReservationException("이미 예약(대기)가 존재하여 예약이 불가능합니다.");
        }
    }

    public ReservationResponse createMemberReservationWithWaitingPayment(WaitingPaymentRequest waitingPaymentRequest, long memberId) {
        Reservation reservation = reservationRepository.findById(waitingPaymentRequest.reservationId())
            .orElseThrow(() -> new InvalidReservationException("존재하지 않는 예약 대기 입니다."));

        validateMyWaiting(memberId, reservation);
        validateStatusIsWaiting(reservation);

        reservation.reserved();

        PaymentRequest request = new PaymentRequest(waitingPaymentRequest.paymentKey(), waitingPaymentRequest.orderId(), waitingPaymentRequest.amount());
        paymentService.approvePayment(request, reservation);

        return new ReservationResponse(reservation);
    }

    private void validateMyWaiting(long memberId, Reservation reservation) {
        Member member = findMemberById(memberId);
        if (!reservation.isReservationOf(member)) {
            throw new ForbiddenException("본인의 예약 대기가 아닙니다.");
        }
    }

    private void validateStatusIsWaiting(Reservation reservation) {
        if (reservation.isReserved()) {
            throw new InvalidReservationException("이미 결제된 예약입니다.");
        }
    }
}


