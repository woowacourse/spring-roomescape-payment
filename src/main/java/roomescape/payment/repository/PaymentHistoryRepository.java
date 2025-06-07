package roomescape.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.PaymentHistory;
import roomescape.reservation.domain.Reservation;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    PaymentHistory findByReservation(Reservation reservation);
}
