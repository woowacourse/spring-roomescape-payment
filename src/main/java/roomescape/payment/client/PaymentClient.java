package roomescape.payment.client;

import roomescape.payment.dto.response.PaymentConfirmResponse;

public interface PaymentClient {

    PaymentConfirmResponse requestPaymentConfirm(String paymentKey, String orderId, Long amount);
}
