package roomescape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
