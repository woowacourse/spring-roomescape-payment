package roomescape.infrastructure;

import org.springframework.stereotype.Repository;
import roomescape.domain.Payment;
import roomescape.domain.repository.PaymentRepository;

@Repository
public class PaymentRepositoryAdaptor implements PaymentRepository {
    private final JpaPaymentRepository jpaPaymentRepository;

    public PaymentRepositoryAdaptor(final JpaPaymentRepository jpaPaymentRepository) {
        this.jpaPaymentRepository = jpaPaymentRepository;
    }

    @Override
    public Payment save(final Payment payment) {
        return jpaPaymentRepository.save(payment);
    }
}
