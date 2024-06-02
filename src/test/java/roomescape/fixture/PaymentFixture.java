package roomescape.fixture;

import roomescape.dto.PaymentRequest;
import roomescape.dto.PaymentResponse;

public class PaymentFixture {

    public static final PaymentRequest DEFAULT_PAYMENT_REQUEST = new PaymentRequest(
            1L, "paymentKey", "orderId", 1000L
    );

    public static final PaymentResponse DEFAULT_PAYMENT_RESPONSE = new PaymentResponse(
            1L, "paymentKey", "orderId", 1000L
    );
}
