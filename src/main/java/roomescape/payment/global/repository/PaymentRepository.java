package roomescape.payment.global.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.global.domain.PgPayment;

public interface PaymentRepository extends JpaRepository<PgPayment, Long> {
}
