package roomescape.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.reservation.payment.ReservationPayment;

public interface JpaReservationPaymentRepository extends JpaRepository<ReservationPayment, Long> {
    Optional<ReservationPayment> findByReservationId(Long reservationId);
}
