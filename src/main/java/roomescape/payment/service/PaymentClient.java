package roomescape.payment.service;

import roomescape.payment.dto.TossPaymentConfirmRequest;

public interface PaymentClient {
    void requestConfirmPayment(TossPaymentConfirmRequest paymentConfirmRequest);

    void cancelPayment(String paymentKey);
}
