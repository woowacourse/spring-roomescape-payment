package roomescape.payment.client;

import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

public interface PaymentClient {

    PaymentResponse confirmPayment(PaymentRequest request);

}
