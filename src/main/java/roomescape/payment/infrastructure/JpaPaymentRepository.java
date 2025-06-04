package roomescape.payment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.Payment;

import java.util.Optional;

public interface JpaPaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservationId(long id);
}
