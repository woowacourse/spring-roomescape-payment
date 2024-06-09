package roomescape.support;

import roomescape.exception.PaymentServerException;
import roomescape.service.PaymentClient;
import roomescape.service.dto.request.PaymentCancelRequest;
import roomescape.service.dto.request.PaymentConfirmRequest;

public class FakePaymentClient implements PaymentClient {
    public static final String PAYMENT_ERROR_KEY = "errorKey";

    @Override
    public void pay(PaymentConfirmRequest paymentConfirmRequest) {
        if (PAYMENT_ERROR_KEY.equals(paymentConfirmRequest.paymentKey())) {
            throw new PaymentServerException("Payment error 발생"); // todo PaymentException
        }
    }

    @Override
    public void cancel(PaymentCancelRequest paymentCancelRequest) {
    }
}
