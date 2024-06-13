package roomescape.domain.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.reservation.Reservation;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByReservationIn(List<Reservation> reservations);

    Optional<Payment> findByReservation(Reservation reservation);
}
