package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.payment.exception.PaymentKeyRequiredException;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class PaymentKey {
    public static final int MAX_LENGTH = 200;

    @Column(name = "payment_key", nullable = false, unique = true)
    private String value;

    public PaymentKey(String value) {
        validatePaymentKey(value);
        this.value = value;
    }

    private static void validatePaymentKey(String value) {
        if (value == null || value.length() > MAX_LENGTH) {
            throw new PaymentKeyRequiredException();
        }
    }
}
