package roomescape.service;

import roomescape.domain.Payment;
import roomescape.dto.PaymentRequest;
import roomescape.exception.PaymentException;

public interface PaymentClient {

    Payment pay(PaymentRequest paymentRequest) throws PaymentException;
}
