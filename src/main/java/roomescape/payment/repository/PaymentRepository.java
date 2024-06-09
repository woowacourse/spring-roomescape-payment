package roomescape.payment.repository;

import org.springframework.data.repository.ListCrudRepository;
import roomescape.payment.domain.Payment;

public interface PaymentRepository extends ListCrudRepository<Payment, Long> {
}
