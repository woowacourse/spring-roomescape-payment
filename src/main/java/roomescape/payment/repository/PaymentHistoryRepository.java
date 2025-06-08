package roomescape.payment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.PaymentHistory;
import roomescape.reservation.domain.Reservation;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    Optional<PaymentHistory> findByReservation(Reservation reservation);
}
