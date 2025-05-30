package roomescape.payment.infrastructure.dto;

import java.math.BigDecimal;
import roomescape.payment.application.dto.PaymentRequest;

public record TossPaymentRequest(
        BigDecimal amount,
        String orderId,
        String paymentKey
) implements PaymentRequest {

}
