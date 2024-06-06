package roomescape.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
