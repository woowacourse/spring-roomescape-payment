package roomescape.fixture;

import roomescape.domain.dto.PaymentRequest;
import roomescape.domain.payment.Payment;

public class PaymentFixture {
    public static PaymentRequest createPaymentRequest() {
        return new PaymentRequest("testKey", "testOrderId", 1000L);
    }

    public static Payment create() {
        return new Payment("paymentKey", 1000L, "orderId", "2024-02-13T12:17:57+09:00", "2024-02-13T12:18:14+09:00");
    }
}
