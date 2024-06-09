package roomescape.support;

import roomescape.payment.PaymentClient;
import roomescape.service.dto.request.PaymentRequest;
import roomescape.payment.TossPaymentResponse;

public class FakePaymentClient implements PaymentClient {

    @Override
    public TossPaymentResponse confirm(PaymentRequest paymentRequest) {
        return new TossPaymentResponse("paymentKey", 1000);
    }
}
