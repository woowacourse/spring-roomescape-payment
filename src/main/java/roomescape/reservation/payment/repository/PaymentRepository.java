package roomescape.reservation.payment.repository;

import org.springframework.data.repository.CrudRepository;

import roomescape.reservation.payment.domain.Payment;
import roomescape.reservation.payment.domain.PaymentId;

public interface PaymentRepository extends CrudRepository<Payment, PaymentId> {
}
