package roomescape.domain;

import roomescape.dto.request.ConfirmPaymentRequest;
import roomescape.dto.response.ConfirmPaymentResponse;
import roomescape.dto.response.RefundPaymentResponse;
import roomescape.entity.Payment;

public interface PaymentClient {

    ConfirmPaymentResponse confirmPayment(ConfirmPaymentRequest confirmPaymentRequest);

    RefundPaymentResponse refund(Payment payment);
}
