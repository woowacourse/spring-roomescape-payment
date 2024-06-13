package roomescape.domain.reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class PaymentInfo {

    @Column(unique = true)
    private String paymentKey;

    @Column(unique = true)
    private String orderId;

    private Integer amount;

    public PaymentInfo(String paymentKey, String orderId, Integer amount) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    protected PaymentInfo() {
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public Integer getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PaymentInfo that = (PaymentInfo) o;
        return Objects.equals(paymentKey, that.paymentKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentKey, orderId);
    }
}
