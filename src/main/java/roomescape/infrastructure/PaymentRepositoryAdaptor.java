package roomescape.infrastructure;

import org.springframework.stereotype.Repository;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.repository.PaymentRepository;

import java.util.Optional;

@Repository
public class PaymentRepositoryAdaptor implements PaymentRepository {

    private final JpaPaymentRepository jpaPaymentRepository;

    public PaymentRepositoryAdaptor(JpaPaymentRepository jpaPaymentRepository) {
        this.jpaPaymentRepository = jpaPaymentRepository;
    }

    @Override
    public Payment save(Payment payment) {
        return jpaPaymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> findByReservation(Reservation reservation) {
        return jpaPaymentRepository.findByReservation(reservation);
    }
}
