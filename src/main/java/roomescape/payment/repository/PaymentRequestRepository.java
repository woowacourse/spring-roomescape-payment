package roomescape.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.PaymentRequest;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, Long> {
}
