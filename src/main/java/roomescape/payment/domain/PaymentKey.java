package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import roomescape.payment.exception.InvalidPaymentException;

@Embeddable
public record PaymentKey(
        @Column(name = "payment_key", nullable = false)
        String value
) {
    public static PaymentKey from(String paymentKey) {
        validate(paymentKey);
        return new PaymentKey(paymentKey);
    }

    private static void validate(String paymentKey) {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new InvalidPaymentException("결제 키는 비어있을 수 없습니다.");
        }
    }
} 
