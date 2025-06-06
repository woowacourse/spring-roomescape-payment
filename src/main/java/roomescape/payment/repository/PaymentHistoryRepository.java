package roomescape.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.PaymentHistory;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
}
