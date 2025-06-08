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
    @Column(name = "payment_key", nullable = false, unique = true)
    private String key;

    public PaymentKey(String key) {
        validatePaymentKey(key);
        this.key = key;
    }

    private static void validatePaymentKey(String paymentKey) {
        if (paymentKey == null || paymentKey.length() > 200) {
            throw new PaymentKeyRequiredException();
        }
    }

}
