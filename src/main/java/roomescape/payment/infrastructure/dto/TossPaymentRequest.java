package roomescape.payment.infrastructure.dto;

import java.math.BigDecimal;
import roomescape.payment.application.dto.PaymentRequest;

public record TossPaymentRequest(
        String paymentKey,
        String orderId,
        BigDecimal amount
) {
    public static TossPaymentRequest from(PaymentRequest request) {
        return new TossPaymentRequest(
                request.paymentKey(),
                request.orderId(),
                request.amount()
        );
    }
}
