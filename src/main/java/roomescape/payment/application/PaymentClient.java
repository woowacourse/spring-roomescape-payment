package roomescape.payment.application;

import roomescape.payment.application.dto.PaymentRequest;
import roomescape.payment.application.dto.PaymentResponse;

public interface PaymentClient {

    PaymentResponse pay(PaymentRequest request);
}
