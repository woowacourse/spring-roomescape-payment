package roomescape.payment;

import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

public interface PaymentClient {

    PaymentResponse confirm(PaymentRequest paymentRequest);
}
