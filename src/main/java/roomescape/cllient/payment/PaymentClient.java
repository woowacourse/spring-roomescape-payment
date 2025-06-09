package roomescape.cllient.payment;

import roomescape.domain.payment.dto.PaymentResult;

public interface PaymentClient {

    PaymentResult authorizePayment(String paymentKey, String orderId, long amount);
}
