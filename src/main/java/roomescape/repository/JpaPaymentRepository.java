package roomescape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.payment.Payment;

public interface JpaPaymentRepository extends JpaRepository<Payment, Long> {
}
