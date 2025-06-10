package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.Reservation;

public record MyReservationResponse(
        Long reservationId,
        String theme,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String status,
        String orderId,
        String paymentKey,
        Long amount
) {

    public MyReservationResponse(final Reservation reservation) {
        this(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                convertToReservationStatusMessage(reservation),
                reservation.getPaymentOrderId().orElse(null),
                reservation.getPaymentKey().orElse(null),
                reservation.getPaymentAmount().orElse(null)
        );
    }

    private static String convertToReservationStatusMessage(final Reservation reservation) {
        StringBuilder status = new StringBuilder();
        if (!reservation.isBooked()) {
            status.append(reservation.getReservationStatus().getRank());
        }
        status.append(reservation.getReservationStatus().getStatus().getOutput());
        return status.toString();
    }
}
