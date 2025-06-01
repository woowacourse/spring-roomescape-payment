package roomescape.payment.infrastructure;

import roomescape.payment.dto.request.PaymentCommand;
import roomescape.payment.dto.response.PaymentResponse;

public interface PaymentClient {
    PaymentResponse authPayment(PaymentCommand paymentCommand);
}
