package roomescape.payment.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class PaymentInfo {

    private String paymentKey;
    private String orderId;
    private Long amount;

    protected PaymentInfo() {
    }

    public PaymentInfo(final String paymentKey, final String orderId, final Long amount) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }
}
