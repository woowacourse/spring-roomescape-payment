package roomescape.client.payment;

import roomescape.mvc.payment.dto.PaymentResult;

public interface PaymentClient {

    PaymentResult authorizePayment(String paymentKey, String orderId, long amount);
}
