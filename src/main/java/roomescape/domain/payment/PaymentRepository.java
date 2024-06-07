package roomescape.domain.payment;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.reservation.Reservation;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @EntityGraph(attributePaths = "reservation")
    List<Payment> findByReservationIn(List<Reservation> reservations);

    Optional<Payment> findByReservation(Reservation reservation);
}
