package roomescape.payment.client;

import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResult;

public interface PaymentClient {

    PaymentResult confirmPayment(PaymentRequest request);

}
