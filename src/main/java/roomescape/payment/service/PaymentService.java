package roomescape.payment.service;

import org.springframework.stereotype.Service;

import roomescape.payment.api.PaymentClient;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

@Service
public class PaymentService {
    private final PaymentClient paymentClient;

    public PaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public PaymentResponse payment(PaymentRequest request) {
        return paymentClient.payment(request);
    }
}
