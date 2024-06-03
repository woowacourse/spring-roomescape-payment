package roomescape.fixture;

import java.util.Map;
import roomescape.payment.dto.PaymentConfirmResponse;

public class PaymentConfirmFixtures {

    private PaymentConfirmFixtures() {
    }

    public static PaymentConfirmResponse getDefaultResponse(String orderId, long amount) {
        return new PaymentConfirmResponse(
                orderId,
                "DONE",
                "orderName",
                amount,
                "2011-12-03T10:15:30+01:00",
                Map.of("provider", "TossPayment")
        );
    }
}
