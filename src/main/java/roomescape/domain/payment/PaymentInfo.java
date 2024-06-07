package roomescape.domain.payment;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Objects;

@Embeddable
public class PaymentInfo {
    private String paymentKey;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    private String orderId;

    private String orderName;

    private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    private Long totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    protected PaymentInfo() {
    }

    public PaymentInfo(String paymentKey, PaymentType type, String orderId, String orderName, String currency,
                       PaymentMethod method, Long totalAmount, PaymentStatus status) {
        this.paymentKey = paymentKey;
        this.type = type;
        this.orderId = orderId;
        this.orderName = orderName;
        this.currency = currency;
        this.method = method;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getTotalAmountWithCurrency() {
        return totalAmount + currency;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public PaymentType getType() {
        return type;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public PaymentStatus getStatus() {
        return status;
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
        return Objects.equals(paymentKey, that.paymentKey) && type == that.type && Objects.equals(
                orderId, that.orderId) && Objects.equals(orderName, that.orderName) && Objects.equals(
                currency, that.currency) && method == that.method && Objects.equals(totalAmount, that.totalAmount)
                && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentKey, type, orderId, orderName, currency, method, totalAmount, status);
    }
}
