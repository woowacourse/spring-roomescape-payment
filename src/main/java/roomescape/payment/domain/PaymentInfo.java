package roomescape.payment.domain;

import jakarta.persistence.Embeddable;
import java.util.Objects;

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

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PaymentInfo that = (PaymentInfo) o;
        return Objects.equals(paymentKey, that.paymentKey) && Objects.equals(
            orderId, that.orderId) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentKey, orderId, amount);
    }
}
