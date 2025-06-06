package roomescape.payment.application.service;

import roomescape.payment.domain.Payment;
import roomescape.payment.application.dto.PaymentRequest;

public interface PaymentClient {

    Payment approve(final PaymentRequest paymentRequest);
}
