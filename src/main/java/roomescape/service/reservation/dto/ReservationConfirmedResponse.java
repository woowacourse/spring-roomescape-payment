package roomescape.service.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.domain.reservation.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationConfirmedResponse(
        Long reservationId,
        String theme,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul") LocalDate date,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul") LocalTime time,
        String status,
        PaymentResponse payment
) {
    public ReservationConfirmedResponse(Reservation reservation) {
        this(reservation.getId(),
                reservation.getTheme().getName().getValue(),
                reservation.getDate(),
                reservation.getTime(),
                reservation.getStatus().getDescription(),
                PaymentResponse.of(reservation.getPayment()));
    }
}
