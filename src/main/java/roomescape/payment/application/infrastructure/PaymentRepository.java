package roomescape.payment.application.infrastructure;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import roomescape.payment.domain.Payment;

@Repository
public interface PaymentRepository extends ListCrudRepository<Payment, Long> {

}
