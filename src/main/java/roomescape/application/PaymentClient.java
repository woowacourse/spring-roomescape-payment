package roomescape.application;

import roomescape.application.dto.request.PaymentRequest;
import roomescape.application.dto.response.PaymentResponse;

public interface PaymentClient {

    PaymentResponse confirmPayment(PaymentRequest paymentRequest);
}
