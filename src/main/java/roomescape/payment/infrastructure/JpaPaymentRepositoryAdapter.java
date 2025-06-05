package roomescape.payment.infrastructure;

import org.springframework.stereotype.Repository;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentRepository;

import java.util.Optional;

@Repository
public class JpaPaymentRepositoryAdapter implements PaymentRepository {

    private final JpaPaymentRepository jpaPaymentRepository;

    public JpaPaymentRepositoryAdapter(JpaPaymentRepository jpaPaymentRepository) {
        this.jpaPaymentRepository = jpaPaymentRepository;
    }

    @Override
    public Optional<Payment> findByReservationId(Long reservationId) {
        return jpaPaymentRepository.findByReservationId(reservationId);
    }

    @Override
    public Payment save(Payment payment) {
        return jpaPaymentRepository.save(payment);
    }

    @Override
    public void deleteByReservationId(Long reservationId) {
        jpaPaymentRepository.deleteByReservationId(reservationId);
    }
}
