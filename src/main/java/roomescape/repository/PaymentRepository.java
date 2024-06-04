package roomescape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.PaymentInfo;

public interface PaymentRepository extends JpaRepository<PaymentInfo, Long> {
}
