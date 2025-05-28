package roomescape.reservation.infrastructure;

import roomescape.reservation.presentation.dto.PaymentRequest;
import roomescape.reservation.presentation.dto.PaymentResponse;

public interface PaymentClient {

    PaymentResponse requestPayment(final PaymentRequest paymentRequest);
}
