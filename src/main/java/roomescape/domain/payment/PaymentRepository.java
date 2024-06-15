package roomescape.domain.payment;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.reservation.Reservation;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findFirstByReservationId(long reservationId);

    Optional<Payment> findFirstByReservation(Reservation reservation);
}
