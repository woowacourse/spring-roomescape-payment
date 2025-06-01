package roomescape.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.payment.ReservationPayment;

public interface ReservationPaymentRepository extends JpaRepository<ReservationPayment, Long> {
}
