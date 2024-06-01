package roomescape.support;

import roomescape.service.PaymentClient;
import roomescape.service.dto.request.PaymentRequest;

public class FakePaymentClient implements PaymentClient {

    @Override
    public void pay(PaymentRequest paymentRequest) {
    }
}
