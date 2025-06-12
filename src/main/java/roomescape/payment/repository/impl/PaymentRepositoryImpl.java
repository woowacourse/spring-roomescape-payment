package roomescape.payment.repository.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.JpaPaymentRepository;
import roomescape.payment.repository.PaymentRepository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final JpaPaymentRepository jpaPaymentRepository;

    @Override
    public Payment save(final Payment payment) {
        return jpaPaymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return jpaPaymentRepository.findById(id);
    }

    @Override
    public Boolean existsByPaymentKey(final String paymentKey) {
        return jpaPaymentRepository.existsByPaymentKey(paymentKey);
    }
}
