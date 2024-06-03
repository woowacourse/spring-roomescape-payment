package roomescape.application.payment;

import org.springframework.stereotype.Service;
import roomescape.application.payment.dto.PaymentRequest;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;

@Service
public class PaymentService {
    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentClient paymentClient,
                          PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public Payment purchase(PaymentRequest request) {
        Payment payment = paymentClient.requestPurchase(request);
        return paymentRepository.save(payment);
    }
}
