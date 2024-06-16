package roomescape.application.dto.response.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.ReservationWithRank;

public record UserReservationResponse(
        long reservationId,
        String themeName,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @JsonFormat(shape = Shape.STRING, pattern = "HH:mm") LocalTime time,
        String status,
        String paymentKey,
        BigDecimal amount,
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
