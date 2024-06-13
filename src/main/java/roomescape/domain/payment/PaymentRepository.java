package roomescape.domain.payment;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.reservation.Reservation;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @EntityGraph(attributePaths = {"reservation"})
    Optional<Payment> findByReservation(Reservation reservation);
}
