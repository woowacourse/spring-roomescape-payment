package roomescape.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentApproveResponse {

    private String paymentKey;
    private String orderId;

    protected PaymentApproveResponse() {
    }

    public PaymentApproveResponse(String paymentKey, String orderId) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
    }

    public void setPaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
