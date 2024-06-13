package roomescape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.reservation.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
