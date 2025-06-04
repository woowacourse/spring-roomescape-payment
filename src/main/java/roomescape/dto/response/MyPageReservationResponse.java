package roomescape.dto.response;

import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationitem.ReservationItem;

import java.time.LocalDate;
import java.time.LocalTime;

public record MyPageReservationResponse(
        Long reservationId,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        int priority,
        String paymentKey,
        Integer amount
) {
    public static MyPageReservationResponse from(final Reservation reservation, final String paymentKey, final Integer amount) {
        ReservationItem item = reservation.getReservationItem();

        return new MyPageReservationResponse(
                reservation.getId(),
                item.getTheme().getName(),
                item.getDate(),
                item.getTime().getStartAt(),
                reservation.getReservationStatus().description,
                reservation.priority(),
                paymentKey,
                amount
        );
    }
}
