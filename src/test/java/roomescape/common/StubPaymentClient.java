package roomescape.common;

import roomescape.payment.domain.PaymentClient;
import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.dto.response.PaymentConfirmResponse;

public class StubPaymentClient implements PaymentClient {

    @Override
    public PaymentConfirmResponse confirm(PaymentConfirmRequest request) {
        return new PaymentConfirmResponse("paymentKey", "orderId", 10);
    }
}
