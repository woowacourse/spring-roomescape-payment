package roomescape.infrastructure.payment;

import roomescape.application.request.PaymentInfo;
import roomescape.application.response.PaymentResponse;

public interface PaymentClient {

    PaymentResponse confirmPayment(PaymentInfo paymentInfo);
}
