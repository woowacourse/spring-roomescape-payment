package roomescape.reservation.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.reservation.domain.CompletedPayment;

public interface CompletedPaymentJpaRepository extends JpaRepository<CompletedPayment, Long> {
    boolean existsByReservationId(Long reservationId);
}
