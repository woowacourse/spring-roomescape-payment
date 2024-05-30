package roomescape.service.payment;

import roomescape.service.payment.dto.PaymentConfirmInput;
import roomescape.service.payment.dto.PaymentConfirmOutput;

public interface PaymentClient {
    PaymentConfirmOutput confirmPayment(PaymentConfirmInput confirmRequest);
}
