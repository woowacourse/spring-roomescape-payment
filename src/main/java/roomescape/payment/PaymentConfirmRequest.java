package roomescape.payment;

import java.math.BigDecimal;
import roomescape.reservation.ReservationRequest;

public class PaymentConfirmRequest {
    private String paymentKey;
    private final String orderId;
    private final Long amount;

    public PaymentConfirmRequest(String paymentKey, String orderId, Long amount) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    public PaymentConfirmRequest(ReservationRequest reservationRequest) {
        this.paymentKey = reservationRequest.getPaymentKey();
        this.orderId = reservationRequest.getOrderId();
        this.amount = reservationRequest.getAmount();
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

    public void setPaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }
}
