package roomescape.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import roomescape.reservation.model.Payment;
import roomescape.reservation.model.Reservation;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByReservation(Reservation reservation);
}
