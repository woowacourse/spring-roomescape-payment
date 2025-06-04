package roomescape.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
