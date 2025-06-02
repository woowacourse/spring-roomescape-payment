package roomescape.infrastructure.payment.toss.dto;

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
}
