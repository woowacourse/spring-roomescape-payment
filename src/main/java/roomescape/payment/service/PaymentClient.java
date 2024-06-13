package roomescape.payment.service;

import roomescape.payment.dto.PaymentConfirmRequest;

public interface PaymentClient {
    void confirmPayment(PaymentConfirmRequest confirmRequest);
}
