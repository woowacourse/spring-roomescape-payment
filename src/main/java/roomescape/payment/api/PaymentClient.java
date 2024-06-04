package roomescape.payment.api;

import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

public interface PaymentClient {
    PaymentResponse payment(PaymentRequest paymentRequest);
}
