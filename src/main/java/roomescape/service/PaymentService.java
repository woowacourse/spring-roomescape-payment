package roomescape.service;

import org.springframework.stereotype.Service;

import roomescape.domain.reservation.payment.Payment;
import roomescape.repository.PaymentRepository;
import roomescape.service.client.PaymentClient;
import roomescape.service.dto.PaymentRequest;

@Service
public class PaymentService {
    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentClient paymentClient, PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public Payment pay(PaymentRequest request) {
        paymentClient.requestPayment(request);

        Payment payment = new Payment(request.orderId(), request.amount(), request.paymentKey());
        return paymentRepository.save(payment);
    }
}
