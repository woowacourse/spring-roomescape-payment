package roomescape.infrastructure.payment.toss.dto;

import java.util.Objects;
import roomescape.infrastructure.payment.dto.PaymentApproveRequest;

public class TossPaymentApproveRequest implements PaymentApproveRequest {

    private final String paymentKey;
    private final String orderId;
    private final Long amount;

    public TossPaymentApproveRequest(String paymentKey, String orderId, Long amount) {
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
        TossPaymentApproveRequest that = (TossPaymentApproveRequest) o;
        return Objects.equals(paymentKey, that.paymentKey) && Objects.equals(orderId, that.orderId)
                && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentKey, orderId, amount);
    }
}
