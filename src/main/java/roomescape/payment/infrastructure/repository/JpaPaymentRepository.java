package roomescape.payment.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.Payment;

public interface JpaPaymentRepository extends JpaRepository<Payment, Long> {

}
