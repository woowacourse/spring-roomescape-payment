package roomescape.payment.infrastructure.db.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.model.entity.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReservationId(Long reservationId);
}
