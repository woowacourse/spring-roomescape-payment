package roomescape.payment.infraStructure;

import roomescape.payment.infraStructure.dto.ConfirmPaymentRequest;
import roomescape.payment.infraStructure.dto.ConfirmPaymentResponse;

public interface PaymentGatewayClient {
    ConfirmPaymentResponse postConfirmPayment(ConfirmPaymentRequest paymentRequest);
}
