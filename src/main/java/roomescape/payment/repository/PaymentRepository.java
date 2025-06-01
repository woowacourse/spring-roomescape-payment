package roomescape.payment.repository;

import org.springframework.data.repository.CrudRepository;

import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentId;

public interface PaymentRepository extends CrudRepository<Payment, PaymentId> {
}
