package roomescape.service;

import org.springframework.stereotype.Service;

import roomescape.config.PaymentClient;
import roomescape.dto.PaymentRequest;

@Service
public class PaymentService {
    private PaymentClient paymentClient;

    public PaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public void pay(PaymentRequest request) {
        paymentClient.approve(request);
    }
}
