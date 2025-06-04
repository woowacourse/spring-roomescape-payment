package roomescape.payment.infrastructure.db;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.payment.infrastructure.db.dao.PaymentJpaRepository;
import roomescape.payment.model.entity.Payment;
import roomescape.payment.model.repository.PaymentRepository;

@Repository
@RequiredArgsConstructor
public class PaymentDbRepository implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(final Payment payment) {
        return paymentJpaRepository.save(payment);
    }
}
