package roomescape.common;

import roomescape.payment.domain.ConfirmedPayment;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.dto.request.PaymentConfirmRequest;

public class StubPaymentClient implements PaymentClient {

    @Override
    public ConfirmedPayment confirm(PaymentConfirmRequest request) {
        return new ConfirmedPayment("paymentKey", "orderId", 10);
    }
}
