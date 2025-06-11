package roomescape.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.business.model.entity.Payment;
import roomescape.business.model.vo.Id;

public interface PaymentRepository extends JpaRepository<Payment, Id> {
}
