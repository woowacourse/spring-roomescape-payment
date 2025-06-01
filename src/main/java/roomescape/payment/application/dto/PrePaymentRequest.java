package roomescape.payment.application.dto;

import java.math.BigDecimal;

public record PrePaymentRequest(
        String orderId,
        String orderName,
        BigDecimal amount
) {
}
