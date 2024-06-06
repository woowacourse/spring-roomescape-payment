package roomescape.infrastructure;

import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.core.dto.payment.PaymentRequest;

public interface PaymentClient {
    PaymentConfirmResponse getPaymentConfirmResponse(final PaymentRequest request);

    void getPaymentCancelResponse(String paymentKey);
}
