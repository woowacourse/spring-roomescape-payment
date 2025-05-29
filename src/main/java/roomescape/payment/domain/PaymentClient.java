package roomescape.payment.domain;

import roomescape.reservation.presentation.dto.PaymentRequest;
import roomescape.payment.infrastructure.dto.PaymentResponse;

public interface PaymentClient {

    PaymentResponse requestPayment(final PaymentRequest paymentRequest);
}
