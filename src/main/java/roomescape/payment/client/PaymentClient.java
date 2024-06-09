package roomescape.payment.client;

import roomescape.payment.dto.request.PaymentCancelRequest;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.dto.response.PaymentCancelResponse;
import roomescape.payment.dto.response.PaymentResponse;

public interface PaymentClient {

    PaymentResponse confirmPayment(PaymentRequest paymentRequest);

    PaymentCancelResponse cancelPayment(PaymentCancelRequest cancelRequest);
}
