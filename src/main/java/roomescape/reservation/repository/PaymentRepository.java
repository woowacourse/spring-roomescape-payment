package roomescape.reservation.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.reservation.entity.Payment;
import roomescape.reservation.entity.Reservation;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByReservationIn(Collection<Reservation> reservations);
}
