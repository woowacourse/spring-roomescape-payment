package roomescape.payment.infraStructure;

import roomescape.payment.infraStructure.dto.request.ConfirmPaymentRequest;
import roomescape.payment.infraStructure.dto.response.ConfirmPaymentResponse;

public interface PaymentGatewayClient {
    ConfirmPaymentResponse postConfirmPayment(ConfirmPaymentRequest paymentRequest);
}
