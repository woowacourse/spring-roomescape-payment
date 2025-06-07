package roomescape.payment.infrastructure;

import org.springframework.data.repository.CrudRepository;
import roomescape.payment.domain.Payment;

public interface PaymentJpaRepository extends CrudRepository<Payment, Long> {
}
