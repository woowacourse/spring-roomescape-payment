package roomescape.infrastructure;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.business.model.entity.Payment;
import roomescape.business.model.vo.Id;

public interface PaymentRepository extends JpaRepository<Payment, Id> {
    Optional<Payment> findByOrderId(String orderId);

    boolean existsByOrderId(String orderId);
}
