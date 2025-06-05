package roomescape.domain.payment;

import java.util.Optional;

public interface PaymentRepository {

    void save(Payment payment);

    Optional<Payment> findByReservationId(Long reservationId);

    void deleteByReservationId(Long reservationId);
}
