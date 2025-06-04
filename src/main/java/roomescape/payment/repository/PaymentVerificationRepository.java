package roomescape.payment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import roomescape.payment.domain.PaymentVerification;

@Repository
public interface PaymentVerificationRepository extends JpaRepository<PaymentVerification, Long> {
    List<PaymentVerification> findByOrderId(String orderId);
}
