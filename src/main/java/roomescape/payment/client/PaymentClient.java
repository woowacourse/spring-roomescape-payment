package roomescape.payment.client;

import roomescape.payment.dto.request.ConfirmPaymentRequest;
import roomescape.payment.model.Payment;

public interface PaymentClient {

    Payment confirm(ConfirmPaymentRequest confirmPaymentRequest);
}
