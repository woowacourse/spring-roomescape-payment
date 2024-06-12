package roomescape.service.reservation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.dto.PaymentRequest;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.payment.Payment;
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
import roomescape.exception.InvalidMemberException;
import roomescape.exception.InvalidReservationException;
import roomescape.service.payment.PaymentService;
import roomescape.service.reservation.dto.AdminReservationRequest;
import roomescape.service.reservation.dto.ReservationRequest;
import roomescape.service.reservation.dto.ReservationResponse;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
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

    @Transactional
    public ReservationResponse createAdminReservation(AdminReservationRequest adminReservationRequest) {
        Reservation reservation = saveReservation(adminReservationRequest.timeId(), adminReservationRequest.themeId(),
                adminReservationRequest.memberId(), adminReservationRequest.date(), ReservationStatus.RESERVED);
        return new ReservationResponse(reservation);
    }

    @Transactional
    public ReservationResponse createMemberReservation(ReservationRequest reservationRequest, long memberId) {
        Reservation reservation = saveReservation(reservationRequest.timeId(), reservationRequest.themeId(), memberId,
                reservationRequest.date(), ReservationStatus.PENDING_PAYMENT);

        PaymentRequest request = new PaymentRequest(reservationRequest.paymentKey(), reservationRequest.orderId(), reservationRequest.amount());
        if (reservation.isPendingPayment()) {
            Payment payment = paymentService.approvePayment(request);
            reservation.paid(payment);
        }

        return new ReservationResponse(reservation);
    }

    private Reservation saveReservation(long timeId, long themeId, long memberId, LocalDate date, ReservationStatus status) {
        ReservationDate reservationDate = ReservationDate.of(date);
        ReservationTime reservationTime = getTimeById(timeId);
        Theme theme = getThemeById(themeId);
        Member member = getMemberById(memberId);
        ReservationDetail reservationDetail = getReservationDetail(reservationDate, reservationTime, theme);
        validateDuplication(reservationDetail);

        return reservationRepository.save(new Reservation(member, reservationDetail, status));
    }

    private ReservationTime getTimeById(long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new InvalidReservationException("더이상 존재하지 않는 시간입니다."));
    }

    private Theme getThemeById(long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new InvalidReservationException("더이상 존재하지 않는 테마입니다."));
    }

    private Member getMemberById(long memberId) {
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
}


