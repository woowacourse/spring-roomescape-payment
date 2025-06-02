package roomescape.payment.infrastructure;

import roomescape.payment.infrastructure.dto.reqeust.PaymentCommand;
import roomescape.payment.infrastructure.dto.response.PaymentResponse;

public interface PaymentClient {
    PaymentResponse authPayment(PaymentCommand paymentCommand);
}
