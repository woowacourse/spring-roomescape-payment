package roomescape.payment.model.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.model.entity.Payment;
import roomescape.payment.model.repository.PaymentRepository;

@RequiredArgsConstructor
@Component
public class PaymentOperation {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment savePayment(final Payment payment) {
        return paymentRepository.save(payment);
    }
}
