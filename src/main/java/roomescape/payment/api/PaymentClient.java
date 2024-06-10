package roomescape.payment.api;

import roomescape.payment.domain.PaymentInfo;
import roomescape.payment.dto.PaymentRequest;

public interface PaymentClient {
    PaymentInfo payment(PaymentRequest paymentRequest);
}
