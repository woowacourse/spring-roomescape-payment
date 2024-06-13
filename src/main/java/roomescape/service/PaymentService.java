package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.controller.dto.PaymentRequest;
import roomescape.repository.PaymentRepository;
import roomescape.domain.reservation.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.service.dto.response.PaymentResponse;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public PaymentService(PaymentRepository paymentRepository, PaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    public void pay(Reservation reservation, PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = paymentClient.pay(paymentRequest);

        Payment payment = paymentResponse.toPayment(reservation);
        paymentRepository.save(payment);
    }
}
