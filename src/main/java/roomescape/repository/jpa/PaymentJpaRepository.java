package roomescape.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.payment.Payment;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservationId(Long reservationId);
}
