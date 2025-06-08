package roomescape.payment.application.dto;

import java.math.BigDecimal;

public record PrePaymentValidRequest(
        String orderId,
        String orderName,
        BigDecimal amount
) {
}
