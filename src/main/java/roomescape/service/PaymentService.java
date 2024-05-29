package roomescape.service;

import org.springframework.stereotype.Service;

import roomescape.config.PaymentClient;
import roomescape.dto.PaymentRequest;
import roomescape.dto.PaymentResponse;

@Service
public class PaymentService {
    private PaymentClient paymentClient;

    public PaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public PaymentResponse payment(PaymentRequest request) {
        paymentClient.approve(request);
        return paymentClient.readPayment(request.paymentKey());
    }
}
