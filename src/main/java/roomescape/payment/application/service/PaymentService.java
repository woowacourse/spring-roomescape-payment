package roomescape.payment.application.service;

import org.springframework.stereotype.Service;
import roomescape.payment.presentation.dto.PaymentRequest;
import roomescape.reservation.presentation.dto.ReservationRequest;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;

    public PaymentService(final PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public void approve(final PaymentRequest paymentRequest) {
        paymentClient.approve(paymentRequest);
    }
}
