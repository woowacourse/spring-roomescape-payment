package roomescape.payment;

import roomescape.payment.dto.PaymentRequest;

public interface PaymentClient {

    void postPayment(PaymentRequest paymentRequest);
}
