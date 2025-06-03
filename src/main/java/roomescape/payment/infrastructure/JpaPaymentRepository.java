package roomescape.payment.infrastructure;

import org.springframework.data.repository.CrudRepository;
import roomescape.payment.domain.Payment;

public interface JpaPaymentRepository extends CrudRepository<Payment,Long>, PaymentRepository {

}
