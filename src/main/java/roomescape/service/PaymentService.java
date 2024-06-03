package roomescape.service;

import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;

public interface PaymentService {
    PaymentResponse pay(PaymentRequest paymentRequest);
}
