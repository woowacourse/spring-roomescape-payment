package roomescape.payment.domain;

import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.dto.response.PaymentResponse;

public interface PaymentClient {

    PaymentResponse requestPayment(final PaymentRequest paymentRequest);
}
