package roomescape.booking;

import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.booking.dto.BookingResponse;
import roomescape.booking.reservation.Reservation;
import roomescape.booking.reservation.ReservationService;
import roomescape.booking.reservation.dto.ReservationPayment;
import roomescape.booking.waiting.Waiting;
import roomescape.booking.waiting.WaitingService;
import roomescape.schedule.Schedule;

@Service
@AllArgsConstructor
public class BookingService {

    private final ReservationService reservationService;
    private final WaitingService waitingService;

    @Transactional(readOnly = true)
    public List<BookingResponse> readAllByMember(final LoginMember loginMember) {
        List<ReservationPayment> reservationPayments = reservationService.getReservationPaymentsByEmail(
                loginMember.email());
        List<Waiting> waitings = waitingService.findAllByEmail(loginMember.email());

        return Stream.concat(
                reservationPayments.stream().map(BookingResponse::of),
                waitings.stream().map((waiting) -> BookingResponse.of(waiting, waitingService.getRank(waiting) + 1))
        ).toList();
    }

    @Transactional
    public void deleteReservationById(final Long id) {
        Reservation oldReservation = reservationService.getById(id);
        reservationService.deleteById(id);

        Schedule schedule = oldReservation.getSchedule();
        if (!waitingService.existsBySchedule(schedule)) {
            return;
        }

        Waiting firstWaiting = waitingService.findFirstWaitingOfSchedule(schedule);
        changeFirstWaitingToReservation(firstWaiting);
    }

    private void changeFirstWaitingToReservation(final Waiting firstWaiting) {
        waitingService.delete(firstWaiting);
        Reservation reservation = new Reservation(firstWaiting.getMember(), firstWaiting.getSchedule(), null);
        reservationService.create(reservation);
    }
}
