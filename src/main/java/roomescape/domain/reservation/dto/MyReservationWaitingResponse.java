package roomescape.domain.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record MyReservationWaitingResponse(
        Long id,
        String theme,
        LocalDate date,
        LocalTime time,
        int order,
        boolean paymentAvailable
) {
    public static MyReservationWaitingResponse from(final ReservationWaitingWithOrderDto reservationWaiting) {
        return new MyReservationWaitingResponse(
                reservationWaiting.id(),
                reservationWaiting.theme().name().getValue(),
                reservationWaiting.date().getValue(),
                reservationWaiting.time().startAt(),
                reservationWaiting.order(),
                reservationWaiting.paymentAvailable()
        );
    }
}
