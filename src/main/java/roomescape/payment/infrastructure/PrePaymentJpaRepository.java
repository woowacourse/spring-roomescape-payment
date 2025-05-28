package roomescape.payment.infrastructure;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.PrePayment;

//TODO: JPA레포 말고 더 가벼운거 상속받을 수 있나?  (2025-05-28, 수, 11:54)
public interface PrePaymentJpaRepository extends JpaRepository<PrePayment, Long> {
    Optional<PrePayment> findByOrderId(String orderId);
}
