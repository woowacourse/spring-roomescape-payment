package roomescape.payment.repository;

import org.springframework.data.repository.CrudRepository;
import roomescape.payment.domain.Payment;


public interface JpaPaymentRepository extends CrudRepository<Payment, Long> {

}
