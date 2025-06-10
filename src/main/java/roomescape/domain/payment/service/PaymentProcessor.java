package roomescape.domain.payment.service;

import roomescape.domain.payment.PaymentType;
import roomescape.domain.payment.dto.PaymentConfirmRequest;
import roomescape.domain.payment.dto.PaymentConfirmResponse;

public interface PaymentProcessor {
    boolean supports(final PaymentType paymentType);

    PaymentConfirmResponse processPayment(final PaymentConfirmRequest request);
}
