package roomescape.reservation.service;

import roomescape.reservation.dto.PaymentConfirmRequest;

public interface PaymentClient {
    void requestConfirmPayment(PaymentConfirmRequest paymentConfirmRequest);
}
