package roomescape.payment.application.dto;

import java.math.BigDecimal;

public record PaymentDataRequest(
        String orderId,
        String orderName,
        BigDecimal amount
) {
}
