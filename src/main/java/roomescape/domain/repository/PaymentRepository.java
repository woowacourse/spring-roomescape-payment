package roomescape.domain.repository;

import java.util.Optional;
import roomescape.domain.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);

    Optional<Payment> findByReservationId(Long reservationId);
}
