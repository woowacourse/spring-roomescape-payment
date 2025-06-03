package roomescape.domain.reservation;

import roomescape.domain.reservation.dto.payment.PaymentRequest;

public interface PaymentManager {

    void confirmPayment(final PaymentRequest paymentRequest);
}
