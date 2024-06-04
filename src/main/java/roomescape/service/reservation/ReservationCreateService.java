package roomescape.service.reservation;

import java.time.LocalDate;
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
        return createReservation(adminReservationRequest.timeId(), adminReservationRequest.themeId(),
            adminReservationRequest.memberId(), adminReservationRequest.date(), Payment.createEmpty());
    }

    public ReservationResponse createMemberReservation(ReservationRequest reservationRequest, long memberId) {
        PaymentRequest request = new PaymentRequest(reservationRequest.paymentKey(), reservationRequest.orderId(), reservationRequest.amount());
        Payment payment = paymentService.approvePayment(request);

        return createReservation(reservationRequest.timeId(), reservationRequest.themeId(), memberId,
            reservationRequest.date(), payment);
    }

    private ReservationResponse createReservation(long timeId, long themeId, long memberId, LocalDate date, Payment payment) {
        ReservationDate reservationDate = ReservationDate.of(date);
        ReservationTime reservationTime = findTimeById(timeId);
        Theme theme = findThemeById(themeId);
        Member member = findMemberById(memberId);
        ReservationDetail reservationDetail = getReservationDetail(reservationDate, reservationTime, theme);
        validateDuplication(reservationDetail);

        Reservation reservation = reservationRepository.save(new Reservation(member, reservationDetail, ReservationStatus.RESERVED, payment));
        return new ReservationResponse(reservation);
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
}


