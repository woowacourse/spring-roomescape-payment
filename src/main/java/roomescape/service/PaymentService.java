package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.Payment;
import roomescape.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentClient paymentClient, PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public Payment pay(Payment payment) {
        paymentClient.pay(payment);
        return paymentRepository.save(payment);
    }
}
