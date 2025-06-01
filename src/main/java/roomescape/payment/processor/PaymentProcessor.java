package roomescape.payment.processor;

import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;

public interface PaymentProcessor {
    boolean supports(final PaymentType paymentType);

    PaymentConfirmResponse processPayment(final PaymentConfirmRequest request);
}
