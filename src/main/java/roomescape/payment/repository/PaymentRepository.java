package roomescape.payment.repository;

import org.springframework.stereotype.Repository;
import roomescape.payment.domain.Payment;

@Repository
public class PaymentRepository implements PaymentRepositoryInterface {

    private final JpaPaymentRepository jpaPaymentRepository;

    public PaymentRepository(JpaPaymentRepository jpaPaymentRepository) {
        this.jpaPaymentRepository = jpaPaymentRepository;
    }

    @Override
    public Payment save(final Payment payment) {
        return jpaPaymentRepository.save(payment);
    }
}
