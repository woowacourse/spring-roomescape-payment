package roomescape.domain;

import roomescape.dto.PaymentRequest;
import roomescape.dto.PaymentResponse;
import roomescape.exception.PaymentException;

public interface PaymentClient {

    PaymentResponse pay(PaymentRequest paymentRequest) throws PaymentException;
}
