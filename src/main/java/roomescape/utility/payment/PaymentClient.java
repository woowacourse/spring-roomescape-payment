package roomescape.utility.payment;

import roomescape.dto.business.PaymentResult;

public interface PaymentClient {

    PaymentResult authorizePayment(String paymentKey, String orderId, long amount);
}
