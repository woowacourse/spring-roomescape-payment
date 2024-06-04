package roomescape.payment.client;

import roomescape.payment.dto.request.ConfirmPaymentRequest;
import roomescape.payment.model.PaymentInfoFromClient;

public interface PaymentClient {

    PaymentInfoFromClient confirm(ConfirmPaymentRequest confirmPaymentRequest);
}
