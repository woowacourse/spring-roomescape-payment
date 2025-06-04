package roomescape.payment.domain;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class PaymentKey {

    private String paymentKey;

    public PaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }

    protected PaymentKey() {
    }

    public String getValue() {
        return paymentKey;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PaymentKey that)) {
            return false;
        }
        return Objects.equals(paymentKey, that.paymentKey);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(paymentKey);
    }
}
