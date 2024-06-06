package roomescape.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.reservation.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
