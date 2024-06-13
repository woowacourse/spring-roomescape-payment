package roomescape.payment;

import roomescape.payment.dto.CancelPaymentRequest;
import roomescape.payment.dto.CreatePaymentRequest;
import roomescape.payment.dto.PaymentConfirmResponse;

public interface PaymentClient {

    PaymentConfirmResponse postPayment(CreatePaymentRequest paymentRequest);

    void cancelPayment(CancelPaymentRequest paymentRequest);
}
