package roomescape.payment.infrastructure;

import org.springframework.stereotype.Repository;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentRepository;

@Repository
public class PaymentJpaRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    public PaymentJpaRepositoryAdapter(final PaymentJpaRepository paymentJpaRepository) {
        this.paymentJpaRepository = paymentJpaRepository;
    }

    @Override
    public Payment save(final Payment payment) {
        return paymentJpaRepository.save(payment);
    }
}
