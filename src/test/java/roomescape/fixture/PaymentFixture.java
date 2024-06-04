package roomescape.fixture;

import roomescape.domain.dto.PaymentRequest;

public class PaymentFixture {
    public static PaymentRequest createPaymentRequest() {
        return new PaymentRequest("testKey", "testOrderId", 1000L);
    }
}
