package roomescape.utility;

import roomescape.dto.business.PaymentResult;

public interface PaymentClient {

    PaymentResult pay(String paymentKey, String orderId, int amount);
}
