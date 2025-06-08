package roomescape.payment.domain;

import roomescape.payment.infrastructure.client.dto.PaymentRequest;
import roomescape.payment.infrastructure.client.dto.PaymentResult;

public interface PaymentClient {

    PaymentResult confirmPayment(PaymentRequest request);

}
