package roomescape.utility;

import roomescape.dto.business.PaymentResult;
import roomescape.exception.PaymentException;

public class PaymentClientStub implements PaymentClient {

    private PaymentResult paymentResult = new PaymentResult("askdkasrwe", "sdfa132", 1000L);
    private String errorCase = null;

    @Override
    public PaymentResult pay(String paymentKey, String orderId, long amount) {
        if (errorCase != null) {
            throw new PaymentException(errorCase);
        }
        return paymentResult;
    }

    public void clearErrorCase() {
        this.errorCase = null;
    }

    public void setErrorCase(String errorCase) {
        this.errorCase = errorCase;
    }

    public void setPaymentResult(PaymentResult paymentResult) {
        this.paymentResult = paymentResult;
    }
}
