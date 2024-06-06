package roomescape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.reservation.Reservation;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReservationAndStatus(final Reservation reservation, final PaymentStatus status);
}
