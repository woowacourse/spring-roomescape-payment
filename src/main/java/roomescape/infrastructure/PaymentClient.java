package roomescape.infrastructure;

import roomescape.core.dto.payment.PaymentCancelResponse;
import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.core.dto.payment.PaymentRequest;

public interface PaymentClient {
    PaymentConfirmResponse getPaymentConfirmResponse(final PaymentRequest request);

    PaymentCancelResponse getPaymentCancelResponse(String paymentKey);
}
