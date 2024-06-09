package roomescape.reservation.dto;

import java.math.BigDecimal;

public record ReservationPaymentRequest(
        String paymentKey,
        String orderId,
        BigDecimal amount
) {
}
