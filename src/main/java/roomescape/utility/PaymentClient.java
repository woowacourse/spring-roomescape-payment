package roomescape.utility;

import roomescape.domain.PaymentResult;

public interface PaymentClient {

    PaymentResult pay(String paymentKey, String orderId, long amount);
}
