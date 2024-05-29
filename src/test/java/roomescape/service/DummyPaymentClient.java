package roomescape.service;

import roomescape.service.payment.PaymentClient;
import roomescape.service.payment.dto.PaymentConfirmRequest;
import roomescape.service.payment.dto.PaymentConfirmResponse;

public class DummyPaymentClient implements PaymentClient {
    @Override
    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest confirmRequest) {
        return new PaymentConfirmResponse(null, null, null, null, null, null, null);
    }
}
