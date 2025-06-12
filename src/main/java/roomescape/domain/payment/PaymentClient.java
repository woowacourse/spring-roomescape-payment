package roomescape.domain.payment;

import roomescape.infrastructure.payment.toss.dto.request.TossPaymentRequest;
import roomescape.infrastructure.payment.toss.dto.response.TossPaymentResponse;

public interface PaymentClient {

    TossPaymentResponse requestPayment(final TossPaymentRequest tossPaymentRequest);
}
