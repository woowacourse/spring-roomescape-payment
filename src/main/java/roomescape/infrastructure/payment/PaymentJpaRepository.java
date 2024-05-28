package roomescape.infrastructure.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.payment.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

}
