package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.infrastructure.payment.PaymentClient;

@Service
public class TossPaymentService implements PaymentService {
    private final PaymentClient paymentClient;

    public TossPaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    @Override
    public PaymentResponse pay(PaymentRequest paymentRequest) {
        return paymentClient.pay(paymentRequest);
    }
}
