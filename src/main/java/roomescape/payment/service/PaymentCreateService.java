package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.repository.PaymentRepository;

@Service
public class PaymentCreateService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentCreateService(PaymentClient paymentClient, PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public void confirmPayment(PaymentConfirmRequest request) {
        paymentClient.confirmPayment(request);
        paymentRepository.save(request.createPayment());
    }
}
