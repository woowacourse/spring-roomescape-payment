package roomescape.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import roomescape.exception.BusinessRuleViolationException;

@Embeddable
public record PaymentKey(
        @Column(name = "payment_key", unique = true, nullable = false, length = MAX_LENGTH)
        String value
) {

    private static final int MAX_LENGTH = 200;

    public PaymentKey {
        if (value == null || value.isBlank() || value.contains(" ")) {
            throw new BusinessRuleViolationException("결제 키값은 공백이거나 공백을 포함할 수 없습니다.");
        }

        if (value.length() > MAX_LENGTH) {
            throw new BusinessRuleViolationException(String.format("결제 키값은 %d자를 넘길 수 없습니다.", MAX_LENGTH));
        }
    }
}
