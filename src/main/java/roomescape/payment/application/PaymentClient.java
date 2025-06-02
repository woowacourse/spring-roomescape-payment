package roomescape.payment.application;

import roomescape.payment.application.dto.PaymentRequest;
import roomescape.payment.application.dto.PaymentResponse;

public interface PaymentClient {

    PaymentResponse requestPayment(PaymentRequest request);
}
