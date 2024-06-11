package roomescape.service.reservation;

import java.time.LocalDate;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationWaiting;
import roomescape.domain.reservation.ReservationWaitingRepository;
import roomescape.domain.schedule.ReservationDate;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.schedule.ReservationTimeRepository;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.exception.InvalidMemberException;
import roomescape.exception.InvalidReservationException;
import roomescape.service.payment.PaymentService;
import roomescape.service.payment.dto.PaymentRequest;
import roomescape.service.reservation.dto.ReservationRequest;
import roomescape.service.reservation.dto.ReservationWaitingResponse;

@Service
public class ReservationWaitingService {
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final ReservationWaitingRepository reservationWaitingRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentService paymentService;

    public ReservationWaitingService(
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository, MemberRepository memberRepository,
            ReservationWaitingRepository reservationWaitingRepository,
            ReservationRepository reservationRepository, PaymentService paymentService
    ) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.reservationWaitingRepository = reservationWaitingRepository;
        this.reservationRepository = reservationRepository;
        this.paymentService = paymentService;
    }

    @Transactional
    public ReservationWaitingResponse create(ReservationRequest waitingRequest, long memberId) {
        ReservationWaiting waiting = generateValidWaiting(waitingRequest, memberId);
        ReservationWaiting confirmedWaiting = confirmPayment(waiting, waitingRequest.toPaymentRequest());
        return new ReservationWaitingResponse(reservationWaitingRepository.save(confirmedWaiting));
    }

    private ReservationWaiting generateValidWaiting(ReservationRequest waitingRequest, long memberId) {
        ReservationDate reservationDate = ReservationDate.of(waitingRequest.date());
        ReservationTime reservationTime = findTimeById(waitingRequest.timeId());
        Schedule schedule = new Schedule(reservationDate, reservationTime);
        Theme theme = findThemeById(waitingRequest.themeId());
        Member member = findMemberById(memberId);
        validate(reservationDate, reservationTime, theme, member, schedule);
        return new ReservationWaiting(member, theme, schedule);
    }

    private ReservationWaiting confirmPayment(ReservationWaiting waiting, PaymentRequest paymentRequest) {
        if (paymentRequest.isAdmin()) {
            return waiting;
        }
        Payment payment = paymentService.confirm(paymentRequest);
        return waiting.withPayment(payment);
    }

    public List<ReservationWaitingResponse> findAll() {
        return reservationWaitingRepository.findAll().stream()
                .map(ReservationWaitingResponse::new)
                .toList();
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

    private void validate(ReservationDate date, ReservationTime reservationTime, Theme theme, Member member,
                          Schedule schedule) {
        if (date.isToday()) {
            throw new InvalidReservationException("방문 당일에는 예약 대기를 등록할 수 없습니다.");
        }
        if (!reservationRepository.existsByScheduleDateAndScheduleTimeIdAndThemeId(date, reservationTime.getId(),
                theme.getId())) {
            throw new InvalidReservationException("현재 해당 테마가 예약 가능하므로 예약 대기는 등록할 수 없습니다.");
        }
        if (reservationWaitingRepository.existsByMemberAndThemeAndSchedule(member, theme, schedule)) {
            throw new InvalidReservationException("이미 해당 테마에 예약 대기를 등록했습니다.");
        }
    }

    @Transactional
    public void deleteById(long waitingId) {
        reservationWaitingRepository.findById(waitingId)
                .ifPresent(this::cancelWaiting);
    }

    @Transactional
    public void deleteById(long waitingId, long memberId) {
        reservationWaitingRepository.findById(waitingId)
                .ifPresent(waiting -> {
                    waiting.checkCancelAuthority(memberId);
                    cancelWaiting(waiting);
                });
    }

    private void cancelWaiting(ReservationWaiting waiting) {
        reservationWaitingRepository.deleteById(waiting.getId());
        if (waiting.isPaid()) {
            paymentService.cancel(waiting.getPayment());
        }
    }

    @Scheduled(cron = "${resetwaiting.schedule.cron}")
    protected void deleteTodayWaiting() {
        List<ReservationWaiting> waitingOfToday = reservationWaitingRepository.findBySchedule_Date(LocalDate.now());
        waitingOfToday.forEach(this::cancelWaiting);
    }
}
