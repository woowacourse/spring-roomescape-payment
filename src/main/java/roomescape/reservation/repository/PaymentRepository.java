package roomescape.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.PaymentKey;

public interface PaymentRepository extends JpaRepository<Payment, PaymentKey> {
}
