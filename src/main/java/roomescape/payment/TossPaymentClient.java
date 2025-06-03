package roomescape.payment;

import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;

public interface TossPaymentClient {

    TossPaymentResponse requestPaymentApprove(final TossPaymentRequest request);
}
