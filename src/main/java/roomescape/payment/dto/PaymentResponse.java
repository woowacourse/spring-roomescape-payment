package roomescape.payment.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record PaymentResponse(
        String paymentKey,
        String status,
        String orderId,
        String orderName,
        ZonedDateTime requestedAt,
        ZonedDateTime approvedAt,
        BigDecimal totalAmount,
        BigDecimal balanceAmount
) {
}
