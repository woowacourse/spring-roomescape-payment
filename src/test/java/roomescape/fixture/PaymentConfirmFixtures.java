package roomescape.fixture;

import roomescape.payment.dto.PaymentConfirmResponse;

public class PaymentConfirmFixtures {

    private PaymentConfirmFixtures() {
    }

    public static PaymentConfirmResponse getDefaultResponse(String paymentKey, long amount) {
        return new PaymentConfirmResponse(paymentKey, amount);
    }
}
