package roomescape.payment.service;

import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse createOrder(PaymentRequest paymentRequest);

    PaymentResponse confirmPayment(PaymentRequest paymentRequest);

    PaymentResponse getPayment(String paymentKey);
}
