package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.client.PaymentClient;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;

@Service
public class TossPaymentService implements PaymentService {
    private final PaymentClient paymentClient;

    public TossPaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public PaymentResponse confirmPayment(PaymentRequest paymentRequest) {
        return paymentClient.requestPayment(paymentRequest);
    }
}
