package roomescape.domain.payment;

import roomescape.application.dto.request.payment.PaymentRequest;
import roomescape.application.dto.response.payment.PaymentResponse;

public interface PaymentClient {

    PaymentResponse confirmPayment(PaymentRequest paymentRequest);

    void cancel(Payment payment, CancelReason request);

}
