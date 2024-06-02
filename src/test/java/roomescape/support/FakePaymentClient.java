package roomescape.support;

import roomescape.application.dto.request.payment.PaymentRequest;
import roomescape.application.dto.response.payment.PaymentResponse;
import roomescape.domain.payment.PaymentClient;

public class FakePaymentClient implements PaymentClient {
    @Override
    public PaymentResponse confirm(PaymentRequest paymentRequest) {
        return new PaymentResponse(
                1000L,
                "paymentKey",
                "orderId",
                "status",
                "requestedAt",
                "approvedAt"
        );
    }
}
