package roomescape.service;

import roomescape.dto.PaymentRequest;
import roomescape.exception.PaymentException;

public interface PaymentClient {

    void pay(PaymentRequest paymentRequest) throws PaymentException;
}
