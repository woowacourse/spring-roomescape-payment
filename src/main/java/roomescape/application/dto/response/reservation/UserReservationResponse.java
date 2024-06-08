package roomescape.application.dto.response.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.ReservationWithRank;

public record UserReservationResponse(
        long reservationId,
        String themeName,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentKey,
        Integer amount,
        long waitingRank
) {

    public static UserReservationResponse from(ReservationWithRank reservation) {
        return new UserReservationResponse(
                reservation.reservationId(),
                reservation.themeName(),
                reservation.date(),
                reservation.time(),
                reservation.status().toString(),
                reservation.paymentKey(),
                reservation.amount(),
                reservation.waitingRank());
    }
}
