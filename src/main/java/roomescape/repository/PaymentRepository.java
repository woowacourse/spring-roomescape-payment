package roomescape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
