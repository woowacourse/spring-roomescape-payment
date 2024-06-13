package roomescape.domain.payment;

import jakarta.persistence.Embeddable;

@Embeddable
public class PaymentKey {

    private String paymentKey;

    protected PaymentKey() {
    }

    public PaymentKey(String value) {
        this.paymentKey = value;
    }

    public String getPaymentKey() {
        return paymentKey;
    }
}
