package roomescape.booking;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.booking.dto.BookingResponse;
import roomescape.booking.reservation.Reservation;
import roomescape.booking.reservation.ReservationPaymentStatus;
import roomescape.booking.reservation.ReservationService;
import roomescape.booking.waiting.Waiting;
import roomescape.booking.waiting.WaitingService;
import roomescape.order.Order;
import roomescape.order.OrderRepository;
import roomescape.order.PaymentStatus;
import roomescape.schedule.Schedule;

@Service
@AllArgsConstructor
public class BookingService {

    private final ReservationService reservationService;
    private final WaitingService waitingService;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<BookingResponse> readAllByMember(final LoginMember loginMember) {
        List<Reservation> reservations = reservationService.getAllByEmail(loginMember.email());
        List<Waiting> waitings = waitingService.findAllByEmail(loginMember.email());

        return Stream.concat(
                reservations.stream().map(BookingResponse::of),
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
        Order order = createOrder(firstWaiting);
        Reservation reservation = new Reservation(
                firstWaiting.getMember(),
                firstWaiting.getSchedule(),
                order,
                ReservationPaymentStatus.WAITING);
        reservationService.create(reservation);
    }

    private Order createOrder(Waiting firstWaiting) {
        Order order = new Order(
                UUID.randomUUID().toString(),
                1000L,
                PaymentStatus.WAITING,
                firstWaiting.getMember(),
                firstWaiting.getSchedule());
        return orderRepository.save(order);
    }
}
