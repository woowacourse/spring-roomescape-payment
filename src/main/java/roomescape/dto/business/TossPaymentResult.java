package roomescape.dto.business;

import roomescape.domain.PaymentResult;

public record TossPaymentResult(String orderId, String paymentKey, Long totalAmount) {

    public PaymentResult toPaymentResult(TossPaymentResult tossPaymentResult, String paymentType) {
        return PaymentResult.createWithoutId(tossPaymentResult.orderId, tossPaymentResult.paymentKey,
                paymentType, tossPaymentResult.totalAmount);
    }
}
