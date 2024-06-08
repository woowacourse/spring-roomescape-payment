package roomescape.support;

import roomescape.exception.PaymentServerException;
import roomescape.service.PaymentClient;
import roomescape.service.dto.request.PaymentRequest;

public class FakePaymentClient implements PaymentClient {
    public static final String PAYMENT_ERROR_KEY = "errorKey";

    @Override
    public void pay(PaymentRequest paymentRequest) {
        if (PAYMENT_ERROR_KEY.equals(paymentRequest.paymentKey())) {
            throw new PaymentServerException("Payment error 발생"); // todo PaymentException
        }
    }
}
