package roomescape.payment.infrastructure.db;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.model.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findTopByReservationIdOrderByCreatedAtDesc(Long reservationId);
}
