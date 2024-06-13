package roomescape.domain.payment;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Objects;

@Embeddable
public class PaymentInfo {
    private String paymentKey;

    private String orderId;

    private String currency;

    private Long totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    protected PaymentInfo() {
    }

    public PaymentInfo(String paymentKey, String orderId, String currency, Long totalAmount, PaymentStatus status) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.currency = currency;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCurrency() {
        return currency;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getTotalAmountWithCurrency() {
        return totalAmount + currency;
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
        return Objects.equals(paymentKey, that.paymentKey) && Objects.equals(orderId, that.orderId)
                && Objects.equals(currency, that.currency) && Objects.equals(totalAmount,
                that.totalAmount) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentKey, orderId, currency, totalAmount, status);
    }
}
