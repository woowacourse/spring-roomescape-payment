package roomescape.payment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.entity.PaymentRequestInfo;

public interface PaymentRequestInfoRepository extends JpaRepository<PaymentRequestInfo, Long> {

    Optional<PaymentRequestInfo> findByOrderId(String orderId);
}
