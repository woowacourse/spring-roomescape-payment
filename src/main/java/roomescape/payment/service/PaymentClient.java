package roomescape.payment.service;

import roomescape.payment.dto.PaymentConfirmRequest;

public interface PaymentClient {
    void requestConfirmPayment(PaymentConfirmRequest paymentConfirmRequest);

    void cancelPayment(String paymentKey);
}
