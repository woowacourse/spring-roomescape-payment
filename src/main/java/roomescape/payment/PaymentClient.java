package roomescape.payment;

import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;

public interface PaymentClient {

    PaymentConfirmResponse requestConfirmPayment(PaymentConfirmRequest paymentConfirmRequest);
}
