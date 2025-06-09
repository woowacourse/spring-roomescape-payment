package roomescape.test.stub;

import roomescape.cllient.payment.PaymentClient;
import roomescape.domain.payment.dto.PaymentResult;

public class PaymentClientStub implements PaymentClient {

    private PaymentResult expectedResultInAuthorizePayment;
    private RuntimeException expectedExceptionInAuthorizePayment;

    public PaymentClientStub() {
        expectedResultInAuthorizePayment =
                new PaymentResult("order_id", "payment_key", 1000L);
    }

    @Override
    public PaymentResult authorizePayment(String paymentKey, String orderId, long amount) {
        if (expectedExceptionInAuthorizePayment != null) {
            throw expectedExceptionInAuthorizePayment;
        }
        return expectedResultInAuthorizePayment;
    }

    public void setAuthorizePayment(PaymentResult paymentResult) {
        this.expectedResultInAuthorizePayment = paymentResult;
        this.expectedExceptionInAuthorizePayment = null;
    }

    public void setAuthorizePayment(RuntimeException exception) {
        this.expectedResultInAuthorizePayment = null;
        this.expectedExceptionInAuthorizePayment = exception;
    }
}
