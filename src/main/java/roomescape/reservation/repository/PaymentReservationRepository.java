package roomescape.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.reservation.domain.PaymentReservation;

public interface PaymentReservationRepository extends JpaRepository<PaymentReservation, Long> {
}
