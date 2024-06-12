package roomescape.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.payment.model.PaymentHistory;
import roomescape.domain.reservation.model.Reservation;

import java.util.Optional;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

    Optional<PaymentHistory> findByReservation(Reservation reservation);
}
