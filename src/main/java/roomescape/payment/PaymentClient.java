package roomescape.payment;

import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;

public interface PaymentClient {

    TossPaymentResponse requestPaymentApprove(final TossPaymentRequest request);
}
