package roomescape.payment.service;

import roomescape.payment.dto.PaymentConfirmRequest;

public interface PaymentService {
    void confirmPayment(PaymentConfirmRequest confirmRequest);
}
