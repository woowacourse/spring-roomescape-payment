package roomescape.payment.domain;

import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);

    Optional<Payment> findById(long id);

    Optional<Payment> findByReservationId(long id);
}
