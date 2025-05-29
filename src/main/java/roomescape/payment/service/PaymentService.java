package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.domain.PaymentClient;
import roomescape.reservation.dto.PaymentRequest;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;

    public PaymentService(final PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public void confirmPayment(final PaymentRequest paymentRequest) {
        paymentClient.requestPayment(paymentRequest);
    }
}
