package roomescape.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.NotFoundException;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.PaymentRepository;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void completePayment(Long id) {
        Payment payment = getPayment(id);
        payment.complete();
    }

    @Transactional
    public void failedPayment(Long id) {
        Payment payment = getPayment(id);
        payment.fail();
    }

    private Payment getPayment(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("결제", id));
    }
}
