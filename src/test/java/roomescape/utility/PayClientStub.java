package roomescape.utility;

import roomescape.dto.business.PaymentResult;

public class PayClientStub implements PaymentClient {

    private PaymentResult paymentResult = new PaymentResult("askdkasrwe", "sdfa132", "NORMAL");

    @Override
    public PaymentResult pay(String paymentKey, String orderId, int amount) {
        return paymentResult;
    }

    public void setPaymentResult(PaymentResult paymentResult) {
        this.paymentResult = paymentResult;
    }
}
