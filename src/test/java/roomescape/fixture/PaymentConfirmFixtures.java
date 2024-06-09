package roomescape.fixture;

import java.time.OffsetDateTime;
import roomescape.payment.dto.PaymentConfirmResponse;

public class PaymentConfirmFixtures {

    private PaymentConfirmFixtures() {
    }

    public static PaymentConfirmResponse getDefaultResponse(String orderId, String paymentKey, long amount) {
        return new PaymentConfirmResponse(orderId, paymentKey, amount, OffsetDateTime.now().toString());
    }
}
