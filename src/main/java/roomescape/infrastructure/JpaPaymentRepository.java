package roomescape.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;

import java.util.Optional;

public interface JpaPaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservation(Reservation reservation);
}
