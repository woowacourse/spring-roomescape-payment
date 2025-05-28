package roomescape.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.repository.jpa.PaymentJpaRepository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository repository;

    @Override
    public void save(Payment payment) {
        repository.save(payment);
    }
}
