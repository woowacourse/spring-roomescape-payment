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
                getOrderId(reservation),
                getPaymentKey(reservation),
                getAmount(reservation)
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

    private static String getOrderId(Reservation reservation) {
        if (reservation.getPayment() == null) {
            return null;
        }
        return reservation.getPayment().getOrderId();
    }

    private static String getPaymentKey(Reservation reservation) {
        if (reservation.getPayment() == null) {
            return null;
        }
        return reservation.getPayment().getPaymentKey();
    }

    private static Long getAmount(Reservation reservation) {
        if (reservation.getPayment() == null) {
            return null;
        }
        return reservation.getPayment().getAmount();
    }
}
