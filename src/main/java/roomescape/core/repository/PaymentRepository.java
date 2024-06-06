package roomescape.core.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.core.domain.Payment;
import roomescape.core.domain.Reservation;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservation(Reservation reservation);

    boolean existsByReservation(Reservation reservation);
}
