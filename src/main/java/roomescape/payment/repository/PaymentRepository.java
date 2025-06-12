package roomescape.payment.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservation(Reservation reservation);
    List<Payment> findAllByReservationIdIn(List<Long> reservationIds);
}
