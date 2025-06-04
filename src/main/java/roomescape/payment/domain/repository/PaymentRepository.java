package roomescape.payment.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByReservation(Reservation reservation);

    void deleteByReservationId(Long reservationId);
}
