package roomescape.payment.model.repository;

import java.util.Optional;
import roomescape.payment.model.entity.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findByReservationId(Long reservationId);
}
