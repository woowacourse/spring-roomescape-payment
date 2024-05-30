package roomescape.service;

import roomescape.service.payment.PaymentClient;
import roomescape.service.payment.dto.PaymentConfirmInput;
import roomescape.service.payment.dto.PaymentConfirmOutput;

public class DummyPaymentClient implements PaymentClient {
    @Override
    public PaymentConfirmOutput confirmPayment(PaymentConfirmInput confirmRequest) {
        return new PaymentConfirmOutput(null, null, null, null, null, null, null);
    }
}
