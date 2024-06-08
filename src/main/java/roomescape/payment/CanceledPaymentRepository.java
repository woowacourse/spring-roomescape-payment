package roomescape.payment;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CanceledPaymentRepository extends JpaRepository<CanceledPayment, Long> {

    Optional<CanceledPayment> findByPaymentKey(String paymentKey);
}
