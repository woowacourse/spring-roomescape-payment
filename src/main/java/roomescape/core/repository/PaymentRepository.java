package roomescape.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.core.domain.Payment;
import roomescape.core.domain.Reservation;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByReservation(Reservation reservation);

    boolean existsByReservation(Reservation reservation);
}
