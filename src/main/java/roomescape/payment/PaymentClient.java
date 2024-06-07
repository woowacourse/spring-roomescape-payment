package roomescape.payment;

import roomescape.service.dto.request.PaymentRequest;
import roomescape.service.dto.response.PaymentResponse;

public interface PaymentClient {

    PaymentResponse confirm(PaymentRequest paymentRequest);
}
