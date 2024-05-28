package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.dto.PaymentRequest;

@Service
public class PaymentService {

    private final TossPaymentClient paymentClient;

    public PaymentService(TossPaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public void pay(PaymentRequest paymentRequest) {
        paymentClient.pay(paymentRequest);
    }
}
