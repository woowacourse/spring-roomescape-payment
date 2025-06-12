package roomescape.payment.domain.repository;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import roomescape.payment.domain.Payment;

@Repository
public interface PaymentRepository extends ListCrudRepository<Payment, Long> {
}
