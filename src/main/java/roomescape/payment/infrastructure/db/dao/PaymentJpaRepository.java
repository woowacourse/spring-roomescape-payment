package roomescape.payment.infrastructure.db.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.model.entity.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

}
