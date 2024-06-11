package roomescape.payment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.PaymentWAL;

public interface PaymentWALRepository extends JpaRepository<PaymentWAL, Long> {

    Optional<PaymentWAL> findByPaymentKey(String paymentKey);
}
