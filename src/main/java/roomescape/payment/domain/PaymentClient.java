package roomescape.payment.domain;

import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

public interface PaymentClient {

    PaymentResponse requestPayment(final PaymentRequest paymentRequest);
}
