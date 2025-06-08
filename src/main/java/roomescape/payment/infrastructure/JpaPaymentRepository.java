package roomescape.payment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.PaymentInfo;

public interface JpaPaymentRepository extends JpaRepository<PaymentInfo,Long> {

}
