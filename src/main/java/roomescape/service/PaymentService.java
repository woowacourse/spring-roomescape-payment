package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.payment.PaymentResponse;
import roomescape.payment.PaymentClient;
import roomescape.service.dto.request.PaymentRequest;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentClient paymentClient, PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public Payment addPayment(PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = paymentClient.confirm(paymentRequest);
        return paymentRepository.save(paymentResponse.toPayment());
    }
}
