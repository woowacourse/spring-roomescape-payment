package roomescape.payment.domain;

import roomescape.payment.dto.request.TossPaymentRequest;
import roomescape.payment.dto.response.TossPaymentResponse;

public interface PaymentClient {

    TossPaymentResponse requestPayment(final TossPaymentRequest tossPaymentRequest);
}
