package roomescape.payment.domain;

import roomescape.payment.infrastructure.dto.request.TossPaymentRequest;
import roomescape.payment.infrastructure.dto.response.TossPaymentResponse;

public interface PaymentClient {

    TossPaymentResponse requestPayment(final TossPaymentRequest tossPaymentRequest);
}
