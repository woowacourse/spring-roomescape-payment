package roomescape.payment.domain;

import roomescape.payment.infrastructure.dto.PaymentRequest;
import roomescape.payment.infrastructure.dto.PaymentResponse;

public interface PaymentClient {

    PaymentResponse requestPayment(final PaymentRequest paymentRequest);
}
