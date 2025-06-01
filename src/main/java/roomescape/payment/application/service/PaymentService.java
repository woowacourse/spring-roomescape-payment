package roomescape.payment.application.service;

import org.springframework.stereotype.Service;
import roomescape.reservation.presentation.dto.ReservationRequest;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;

    public PaymentService(final PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public void approve(final ReservationRequest reservationRequest) {
        paymentClient.approve(reservationRequest);
    }
}
