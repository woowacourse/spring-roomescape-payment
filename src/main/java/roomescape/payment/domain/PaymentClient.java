package roomescape.payment.domain;

import roomescape.reservation.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

public interface PaymentClient {

    PaymentResponse requestPayment(final PaymentRequest paymentRequest);
}
