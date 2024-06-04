package roomescape.service;

import org.springframework.stereotype.Service;

import roomescape.controller.dto.CreateUserReservationRequest;
import roomescape.service.client.PaymentClient;
import roomescape.service.dto.PaymentRequest;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;

    public PaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public void pay(CreateUserReservationRequest request) {
        paymentClient.requestPayment(new PaymentRequest(request.orderId(), request.amount(), request.paymentKey()));
    }
}
