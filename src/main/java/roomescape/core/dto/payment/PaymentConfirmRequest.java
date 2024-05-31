package roomescape.core.dto.payment;

import roomescape.core.dto.reservation.ReservationPaymentRequest;

public class PaymentConfirmRequest {
    private Long amount;
    private String orderId;
    private String paymentKey;

    public PaymentConfirmRequest(ReservationPaymentRequest request) {
        this.amount = request.getAmount();
        this.orderId = request.getOrderId();
        this.paymentKey = request.getPaymentKey();
    }

    public PaymentConfirmRequest() {
    }

    public Long getAmount() {
        return amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }
}
