package roomescape.fixture;

import roomescape.domain.dto.PaymentRequest;
import roomescape.domain.payment.Payment;

public class PaymentFixture {
    public static PaymentRequest createPaymentRequest() {
        return new PaymentRequest("testKey", "testOrderId", 1000L);
    }

    public static Payment create() {
        return new Payment("paymentKey", 1000L, "orderId", "", "");
    }
}
