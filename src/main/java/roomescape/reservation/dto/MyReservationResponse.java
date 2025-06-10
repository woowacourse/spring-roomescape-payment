package roomescape.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.Reservation;

public record MyReservationResponse(
        Long reservationId,
        String theme,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String status,
        String paymentKey,
        Long amount
) {

    public MyReservationResponse(final Reservation reservation, final Payment payment) {
        this(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                convertToReservationStatusMessage(reservation),
                payment != null ? payment.getPaymentKey() : null,
                payment != null ? payment.getAmount() : null
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
