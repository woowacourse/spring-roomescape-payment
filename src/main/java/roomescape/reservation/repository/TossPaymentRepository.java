package roomescape.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.reservation.domain.TossPayment;

public interface TossPaymentRepository extends JpaRepository<TossPayment, Long> {
}
