package roomescape.core.dto.payment;

import roomescape.core.dto.reservation.ReservationPaymentRequest;

public class PaymentConfirmRequest {
    private Integer amount;
    private String orderId;
    private String paymentKey;

    public PaymentConfirmRequest(ReservationPaymentRequest request) {
        this.amount = request.getAmount();
        this.orderId = request.getOrderId();
        this.paymentKey = request.getPaymentKey();
    }

    public PaymentConfirmRequest() {
    }

    public Integer getAmount() {
        return amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }
}
