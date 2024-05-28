package roomescape.infra.repository;

import org.springframework.data.repository.Repository;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;

public interface PaymentJpaRepository extends
        PaymentRepository,
        Repository<Payment, Long> {

    @Override
    Payment save(Payment payment);
}
