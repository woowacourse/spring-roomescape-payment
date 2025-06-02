package roomescape.domain;

import roomescape.dto.request.ConfirmPaymentRequest;
import roomescape.dto.response.ConfirmPaymentResponse;

public interface PaymentClient {

    ConfirmPaymentResponse confirmPayment(ConfirmPaymentRequest confirmPaymentRequest);
}
