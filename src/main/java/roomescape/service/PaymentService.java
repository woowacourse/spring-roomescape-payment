package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.config.PaymentClient;
import roomescape.dto.PaymentRequest;
import roomescape.dto.PaymentResponse;
import roomescape.entity.Payment;
import roomescape.repository.PaymentRepository;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public PaymentService(PaymentRepository paymentRepository, PaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    public Payment pay(PaymentRequest request, long reservationId) {
        PaymentResponse paymentResponse = paymentClient.approve(request);
        Payment payment = paymentResponse.toModel(reservationId);
        return paymentRepository.save(payment);
    }
}
