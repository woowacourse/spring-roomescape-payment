package roomescape.infrastructure.db;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.model.TossPayment;

public interface TossPaymentJpaRepository extends JpaRepository<TossPayment, Long> {
}
