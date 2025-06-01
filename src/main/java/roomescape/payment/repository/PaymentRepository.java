package roomescape.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentKey;

public interface PaymentRepository extends JpaRepository<Payment, PaymentKey> {
}
