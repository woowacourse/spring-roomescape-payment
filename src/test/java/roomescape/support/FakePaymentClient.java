package roomescape.support;

import roomescape.payment.PaymentClient;
import roomescape.service.dto.request.PaymentRequest;

public class FakePaymentClient implements PaymentClient {

    @Override
    public void confirm(PaymentRequest paymentRequest) {
    }
}
