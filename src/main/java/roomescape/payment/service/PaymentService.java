package roomescape.payment.service;

import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse confirmPayment(PaymentRequest paymentRequest);

    PaymentResponse getPayment(String paymentKey);
}
