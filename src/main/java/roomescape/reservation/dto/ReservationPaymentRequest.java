package roomescape.reservation.dto;

import java.time.LocalDate;

public record ReservationPaymentRequest(
        LocalDate date,
        long themeId,
        long timeId,
        String paymentKey,
        String orderId,
        long amount) {
}
