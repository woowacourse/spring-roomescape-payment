package roomescape.payment.application;

import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.payment.infrastructure.PaymentRepository;

@Service
public class PaymentDataService {

    private final PaymentRepository paymentRepository;

    public PaymentDataService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }
}
