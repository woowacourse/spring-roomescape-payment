package roomescape.infrastructure.persistence.jpa;

import org.springframework.data.repository.CrudRepository;
import roomescape.domain.payment.Payment;

public interface PaymentJpaRepository extends CrudRepository<Payment, Long> {
}
