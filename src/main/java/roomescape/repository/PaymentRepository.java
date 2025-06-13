package roomescape.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findFirstByReservationOrderByCreatedAtDesc(Reservation reservation);
}
