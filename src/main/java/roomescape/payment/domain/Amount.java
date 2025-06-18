package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import roomescape.payment.exception.InvalidPaymentException;

@Embeddable
public record Amount(
        @Column(name = "amount", nullable = false)
        Long value
) {
    public static Amount from(Long amount) {
        validate(amount);
        return new Amount(amount);
    }

    private static void validate(Long amount) {
        if (amount == null) {
            throw new InvalidPaymentException("결제 금액은 null일 수 없습니다.");
        }
        if (amount <= 0) {
            throw new InvalidPaymentException("결제 금액은 0보다 커야 합니다.");
        }
    }
} 
