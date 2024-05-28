package roomescape.domain.payment;

import roomescape.application.dto.request.payment.PaymentCancelRequest;
import roomescape.application.dto.request.payment.PaymentRequest;
import roomescape.application.dto.response.payment.PaymentResponse;

public interface PaymentClient {

    PaymentResponse confirm(PaymentRequest paymentRequest);

    PaymentResponse cancel(String paymentKey, PaymentCancelRequest request);
}
