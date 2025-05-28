package roomescape.payment.infrastructure.dto;

import java.math.BigDecimal;
import roomescape.payment.application.dto.PaymentRequest;

public record TossPaymentRequest(
        BigDecimal amount,
        String orderId,
        String paymentKey
) {

    public static TossPaymentRequest from(final PaymentRequest request) {
        return new TossPaymentRequest(request.amount(), request.orderId(), request.paymentKey());
    }
}
