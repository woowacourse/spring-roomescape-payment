package roomescape.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.booking.dto.BookingResponse;
import roomescape.booking.dto.PromotingReservationRequest;
import roomescape.booking.reservation.Reservation;
import roomescape.booking.reservation.ReservationCreateService;
import roomescape.booking.reservation.ReservationService;
import roomescape.booking.waiting.Waiting;
import roomescape.booking.waiting.WaitingService;
import roomescape.reservationpayment.ReservationPayment;
import roomescape.reservationpayment.ReservationPaymentService;
import roomescape.schedule.Schedule;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final ReservationService reservationService;
    private final ReservationCreateService reservationCreateService;
    private final WaitingService waitingService;
    private final ReservationPaymentService reservationPaymentService;

    @Transactional(readOnly = true)
    public List<BookingResponse> readAllByMember(final LoginMember loginMember) {
        List<Reservation> reservations = reservationService.getAllByEmail(loginMember.email());
        List<Waiting> waitings = waitingService.findAllByEmail(loginMember.email());

        return Stream.concat(
                reservations.stream().map(reservation -> {
                    ReservationPayment reservationPayment = reservationPaymentService.getByReservationId(reservation.getId());
                    return BookingResponse.of(reservation, reservationPayment);
                }),
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
        PromotingReservationRequest request = PromotingReservationRequest.from(firstWaiting);
        reservationCreateService.promote(request);
    }
}
