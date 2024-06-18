package roomescape.payment.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.CanceledPayment;

public interface CanceledPaymentRepository extends JpaRepository<CanceledPayment, Long> {

    Optional<CanceledPayment> findByPaymentKey(String paymentKey);
}
