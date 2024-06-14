package roomescape.reservation.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.reservation.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
