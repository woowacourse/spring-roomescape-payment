package roomescape.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.core.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
