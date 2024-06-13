package roomescape.support;

import roomescape.infrastructure.TossPaymentResponse;
import roomescape.payment.PaymentClient;
import roomescape.service.dto.request.PaymentRequest;

public class FakePaymentClient implements PaymentClient {

    @Override
    public TossPaymentResponse confirm(PaymentRequest paymentRequest) {
        return new TossPaymentResponse("paymentKey", 1000);
    }
}
