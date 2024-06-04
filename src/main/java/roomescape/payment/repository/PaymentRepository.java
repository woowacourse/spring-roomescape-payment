package roomescape.payment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.model.Payment;
import roomescape.reservation.model.Reservation;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReservation(Reservation reservation);
}
