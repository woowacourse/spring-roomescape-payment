package roomescape.fixture;

import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

public class PaymentFixture {
    public static final PaymentRequest PAYMENT_REQUEST = new PaymentRequest("paymentKey", "orderId", 1000);
    public static final PaymentResponse PAYMENT_RESPONSE = new PaymentResponse("orderName", "requestedAt", "approvedAt", "currency", 10000);
}
