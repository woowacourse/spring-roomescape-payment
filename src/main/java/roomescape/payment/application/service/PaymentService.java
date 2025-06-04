package roomescape.payment.application.service;

import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.repository.PaymentRepository;
import roomescape.payment.presentation.dto.PaymentRequest;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public PaymentService(final PaymentRepository paymentRepository, final PaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    public Payment approve(final PaymentRequest paymentRequest) {
        Payment payment =  paymentClient.approve(paymentRequest);
        paymentRepository.save(payment);

        return payment;
    }
}
