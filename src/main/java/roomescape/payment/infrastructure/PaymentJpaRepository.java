package roomescape.payment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
