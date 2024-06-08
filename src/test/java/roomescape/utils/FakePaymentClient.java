package roomescape.utils;

import org.springframework.context.annotation.Profile;
import roomescape.core.dto.payment.PaymentCancelResponse;
import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.core.dto.payment.PaymentRequest;
import roomescape.infrastructure.PaymentClient;

@Profile("test")
public class FakePaymentClient implements PaymentClient {
    @Override
    public PaymentConfirmResponse getPaymentConfirmResponse(final PaymentRequest memberRequest) {
        return new PaymentConfirmResponse(1L, 1000, "orderId", "paymentKey");
    }

    @Override
    public PaymentCancelResponse getPaymentCancelResponse(final String paymentKey) {
        return new PaymentCancelResponse(1L, 1000, "orderId", "paymentKey");
    }
}