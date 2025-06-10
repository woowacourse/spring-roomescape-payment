package roomescape.reservation.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import roomescape.auth.dto.LoginMember;
import roomescape.exception.ReservationException;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.client.PaymentClient;
import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.dto.AdminReservationRequest;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.PaymentApprovalRequest;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSearchRequest;
import roomescape.reservation.repository.PaymentRepository;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.dto.AvailableReservationTimeResponse;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final Clock clock;
    private final PaymentClient paymentClient;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;

    public List<ReservationResponse> findReservationsByCriteria(final ReservationSearchRequest request) {
        final List<Reservation> reservations = reservationRepository.findByCriteria(request.themeId(),
                request.memberId(), request.dateFrom(), request.dateTo());
        return reservations.stream()
                .map(ReservationResponse::new)
                .toList();
    }

    public List<AvailableReservationTimeResponse> findAllReservationTime(final LocalDate date, final Long themeId) {
        return reservationTimeRepository.findAllAvailable(date, themeId);
    }

    public ReservationResponse saveReservation(final ReservationRequest request, final LoginMember loginMember) {
        final ReservationTime reservationTime = reservationTimeRepository.getById(request.timeId());
        final Theme theme = themeRepository.getById(request.themeId());
        final Payment payment = Payment.of(request.paymentKey(), request.orderId(), request.amount());
        final Member member = Member.from(loginMember);
        validateAlreadyBookedMember(request, reservationTime, theme, member);

        paymentClient.approvePayment(
                new PaymentApprovalRequest(request.paymentKey(), request.orderId(), request.amount()));

        if (reservationRepository.existsByDateAndTimeAndTheme(request.date(), reservationTime, theme)) {
            return new ReservationResponse(
                    waitingReservation(request.date(), reservationTime, theme, member, payment));
        }
        return new ReservationResponse(bookedReservation(request.date(), reservationTime, theme, member, payment));
    }

    private void validateAlreadyBookedMember(ReservationRequest request, ReservationTime reservationTime, Theme theme,
            Member member) {
        if (reservationRepository.existsByDateAndTimeAndThemeAndMember(request.date(), reservationTime, theme,
                member)) {
            throw new IllegalArgumentException("이미 예약한 사용자입니다.");
        }
    }

    private Reservation waitingReservation(LocalDate date, ReservationTime reservationTime,
            Theme theme, Member member, Payment payment) {
        Long lastWaitingRank = reservationRepository.getLastWaitingRank(theme, date, reservationTime).orElse(0L);
        Reservation reservation = Reservation.waiting(date, reservationTime, theme, member, LocalDateTime.now(clock),
                lastWaitingRank + 1);
        payment.setReservation(reservation);

        return reservationRepository.save(reservation);
    }

    private Reservation bookedReservation(LocalDate date, ReservationTime reservationTime,
            Theme theme, Member member, Payment payment) {
        Reservation reservation = Reservation.booked(date, reservationTime, theme, member, LocalDateTime.now(clock));
        payment.setReservation(reservation);

        return reservationRepository.save(reservation);
    }

    public ReservationResponse saveAdminReservation(final AdminReservationRequest request) {
        final ReservationTime reservationTime = reservationTimeRepository.getById(request.timeId());
        final Theme theme = themeRepository.getById(request.themeId());
        final Member member = memberRepository.getById(request.memberId());
        if (reservationRepository.existsByDateAndTimeAndTheme(request.date(), reservationTime, theme)) {
            throw new ReservationException("해당 시간은 이미 예약되어있습니다.");
        }
        final Reservation reservation = Reservation.booked(request.date(), reservationTime, theme, member,
                LocalDateTime.now(clock));
        final Reservation newReservation = reservationRepository.save(reservation);
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
    }

    private void reduceWaitingRanks(final Long deleteRank, final List<ReservationStatus> reservationStatuses) {
        reservationStatuses.stream()
                .filter(waiting -> waiting.getRank() != null)
                .filter(waiting -> waiting.getRank() > deleteRank)
                .forEach(ReservationStatus::reduceRank);
    }

    public List<MyReservationResponse> findMyReservations(final LoginMember loginMember) {
        final Member member = Member.from(loginMember);
        List<Reservation> reservations = reservationRepository.findAllByMember(member);

        return reservations.stream()
                .map(reservation -> new MyReservationResponse(reservation,
                        paymentRepository.findByReservation(reservation)))
                .toList();
    }
}
