package roomescape.payment.pg;

import org.springframework.boot.test.context.TestComponent;
import roomescape.payment.dto.PaymentConfirmResponse;

import java.util.Map;

@TestComponent
public class FakePaymentGateway implements PaymentGateway {

    @Override
    public PaymentConfirmResponse confirm(final String orderId, final Long amount, final String paymentKey) {
        return new PaymentConfirmResponse(
                "test-order-id",
                "DONE",
                "테스트 결제",
                10000L,
                "2024-02-13T12:18:14+09:00",
                Map.of("provider", "토스페이")
        );
    }
}
