package roomescape.payment;

import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentProvider;

public interface PaymentResponse {

    PaymentProvider getPaymentProvider();

    String getProviderPaymentId();

    int getAmount();

    default Payment toPayment() {
        return new Payment(getPaymentProvider(), getProviderPaymentId(), getAmount());
    }
}
