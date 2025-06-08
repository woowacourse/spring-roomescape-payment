package roomescape.external.payment;

import roomescape.domain.PaymentResult;

public interface PaymentClient {

    PaymentResult pay(String paymentKey, String orderId, long amount, String paymentType);
}
