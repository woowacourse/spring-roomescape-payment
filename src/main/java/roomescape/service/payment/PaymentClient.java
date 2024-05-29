package roomescape.service.payment;

import roomescape.service.payment.dto.PaymentConfirmRequest;
import roomescape.service.payment.dto.PaymentConfirmResponse;

public interface PaymentClient {
    PaymentConfirmResponse confirmPayment(PaymentConfirmRequest confirmRequest);
}
