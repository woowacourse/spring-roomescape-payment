package roomescape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByReservation(Reservation reservation);
}
