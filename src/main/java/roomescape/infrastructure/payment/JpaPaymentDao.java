package roomescape.infrastructure.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.business.model.entity.Payment;
import roomescape.business.model.vo.Id;

public interface JpaPaymentDao extends JpaRepository<Payment, Id> {

}
