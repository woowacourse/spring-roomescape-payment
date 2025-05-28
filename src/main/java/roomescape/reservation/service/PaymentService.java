package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import roomescape.reservation.infrastructure.PaymentClient;
import roomescape.reservation.presentation.dto.PaymentRequest;

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
