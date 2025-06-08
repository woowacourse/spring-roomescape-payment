package roomescape.reservation.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.auth.application.LoginMember;
import roomescape.global.config.Performance;
import roomescape.global.exception.ReservationException;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.member.service.MemberService;
import roomescape.payment.application.Payment;
import roomescape.reservation.config.PaymentClient;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.dto.AdminReservationRequest;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.PaymentApprovalRequest;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSearchRequest;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.dto.AvailableReservationTimeResponse;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final Clock clock;
    private final PaymentClient paymentClient;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @Performance
    public List<ReservationResponse> findReservationsByCriteria(final ReservationSearchRequest request) {
        List<Reservation> reservations = reservationRepository.findByCriteria(request.themeId(),
                request.memberId(), request.dateFrom(), request.dateTo());
        return reservations.stream()
                .map(ReservationResponse::new)
                .toList();
    }

    @Performance
    public List<AvailableReservationTimeResponse> findAllReservationTime(final LocalDate date, final Long themeId) {
        return reservationTimeRepository.findAllAvailable(date, themeId);
    }

    public ReservationResponse saveReservation(final ReservationRequest request, final LoginMember loginMember) {
        ReservationTime reservationTime = reservationTimeRepository.getById(request.timeId());
        Theme theme = themeRepository.getById(request.themeId());
        Payment payment = Payment.builder()
                .paymentKey(request.paymentKey())
                .orderId(request.orderId())
                .amount(request.amount())
                .build();

        paymentClient.approvePayment(
                new PaymentApprovalRequest(request.paymentKey(), request.orderId(), request.amount()));

        if (reservationRepository.existsByDateAndTimeAndTheme(request.date(), reservationTime, theme)) {
            return new ReservationResponse(
                    waitingReservation(request.date(), reservationTime, theme, loginMember, payment));
        }
        return new ReservationResponse(bookedReservation(request.date(), reservationTime, theme, loginMember, payment));
    }

    private Reservation waitingReservation(LocalDate date, ReservationTime reservationTime,
                                           Theme theme, LoginMember loginMember, Payment payment) {
        Member member = memberService.getMemberById(loginMember.getId());
        if (reservationRepository.existsByDateAndTimeAndThemeAndMember(date, reservationTime, theme, member)) {
            log.warn("[예약 검증 실패] 사용자 중복 예약 시도 - memberId: {}, date: {}, time: {}, theme: {}",
                    member.getId(), date, reservationTime.getStartAt(), theme.getName());
            throw new IllegalArgumentException("이미 예약한 사용자입니다.");
        }
        Long lastWaitingRank = reservationRepository.getLastWaitingRank(theme, date, reservationTime).orElse(0L);
        Reservation reservation = Reservation.waiting(date, reservationTime, theme, member, LocalDateTime.now(clock),
                lastWaitingRank + 1, payment);

        Reservation newReservation = reservationRepository.save(reservation);
        log.info("[(유저) 예약 대기 성공] reservationId: {}", newReservation.getId());
        return newReservation;
    }

    private Reservation bookedReservation(LocalDate date, ReservationTime reservationTime,
                                          Theme theme, LoginMember loginMember, Payment payment) {
        Member member = memberService.getMemberById(loginMember.getId());
        Reservation reservation = Reservation.of(date, reservationTime, theme, member, LocalDateTime.now(clock),
                payment);

        Reservation newReservation = reservationRepository.save(reservation);
        log.info("[(유저) 예약 성공] reservationId: {}", newReservation.getId());
        return newReservation;
    }

    public ReservationResponse saveAdminReservation(final AdminReservationRequest request) {
        ReservationTime reservationTime = reservationTimeRepository.getById(request.timeId());
        Theme theme = themeRepository.getById(request.themeId());
        Member member = memberRepository.getById(request.memberId());
        if (reservationRepository.existsByDateAndTimeAndTheme(request.date(), reservationTime, theme)) {
            log.warn("[예약 검증 실패] 이미 예약된 시간대 - date: {}, time: {}, theme: {}",
                    request.date(), reservationTime.getStartAt(), theme.getName());
            throw new ReservationException("해당 시간은 이미 예약되어있습니다.");
        }
        Reservation reservation = Reservation.of(request.date(), reservationTime, theme, member,
                LocalDateTime.now(clock), null);
        Reservation newReservation = reservationRepository.save(reservation);
        log.info("[(관리자) 예약 성공] reservationId: {}", newReservation.getId());
        return new ReservationResponse(newReservation);
    }

    public void deleteReservation(final Long id) {
        Reservation reservation = reservationRepository.getById(id);
        Long deleteRank = reservation.getReservationStatus().getRank();
        if (reservation.isBooked()) {
            deleteRank = 0L;
        }
        List<ReservationStatus> reservationStatuses = reservationRepository.findAllWaiting(reservation.getDate(),
                reservation.getTime(),
                reservation.getTheme());
        reduceWaitingRanks(deleteRank, reservationStatuses);
        reservationRepository.deleteById(id);
        log.info("[예약 삭제 성공] reservationId: {}", reservation.getId());
    }

    private void reduceWaitingRanks(final Long deleteRank, final List<ReservationStatus> reservationStatuses) {
        reservationStatuses.stream()
                .filter(waiting -> waiting.getRank() != null)
                .filter(waiting -> waiting.getRank() > deleteRank)
                .forEach(ReservationStatus::reduceRank);
    }

    @Performance
    public List<MyReservationResponse> findMyReservations(final LoginMember loginMember) {
        Member member = memberService.getMemberById(loginMember.getId());
        return reservationRepository.findAllByMember(member).stream()
                .map(MyReservationResponse::new)
                .toList();
    }
}
