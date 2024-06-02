package roomescape.payment;

import roomescape.payment.dto.PaymentConfirmRequest;

public interface PaymentClient {

    void requestConfirmPayment(PaymentConfirmRequest paymentConfirmRequest);
}
