package roomescape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.reservation.payment.ReservationPayment;

public interface JpaReservationPaymentRepository extends JpaRepository<ReservationPayment, Long> {
}
