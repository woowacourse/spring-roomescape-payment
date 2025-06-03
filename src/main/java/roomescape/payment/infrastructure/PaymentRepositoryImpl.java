package roomescape.payment.infrastructure;

import org.springframework.stereotype.Repository;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentRepository;

import java.util.Optional;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {
    private JpaPaymentRepository jpaPaymentRepository;

    public PaymentRepositoryImpl(JpaPaymentRepository jpaPaymentRepository) {
        this.jpaPaymentRepository = jpaPaymentRepository;
    }

    @Override
    public Payment save(Payment payment) {
        return jpaPaymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(long id) {
        return jpaPaymentRepository.findById(id);
    }

    @Override
    public Optional<Payment> findByReservationId(long id) {
        return jpaPaymentRepository.findByReservationId(id);
    }
}
