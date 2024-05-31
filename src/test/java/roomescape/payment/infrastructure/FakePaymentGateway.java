package roomescape.payment.infrastructure;

import java.util.Map;
import org.springframework.boot.test.context.TestComponent;
import roomescape.payment.dto.PaymentConfirmResponse;

@TestComponent
public class FakePaymentGateway implements PaymentGateway {

    @Override
    public PaymentConfirmResponse confirm(String orderId, Long amount, String paymentKey) {
        return new PaymentConfirmResponse(
                orderId,
                "DONE",
                "주문명",
                amount,
                "2024-05-29T15:53:19+09:00",
                Map.of("provider", "paymentKey")
        );
    }
}
