package roomescape.payment;

import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.TossPaymentConfirmResponse;

public interface PaymentClient {

    TossPaymentConfirmResponse postPayment(PaymentRequest paymentRequest);
}
