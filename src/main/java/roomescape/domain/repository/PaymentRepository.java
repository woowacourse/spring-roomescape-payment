package roomescape.domain.repository;

import roomescape.domain.Payment;
import roomescape.domain.Reservation;

import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);

    Optional<Payment> findByReservation(Reservation reservation);
}
