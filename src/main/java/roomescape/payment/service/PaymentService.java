package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.dto.PaymentRequest;

@Service
public class PaymentService {

    private final TossPaymentClient tossPaymentClient;

    public PaymentService(TossPaymentClient tossPaymentClient) {
        this.tossPaymentClient = tossPaymentClient;
    }

    public void pay(PaymentRequest paymentRequest) {
        tossPaymentClient.requestPayment(paymentRequest);
    }
}
