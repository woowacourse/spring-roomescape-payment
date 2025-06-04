package roomescape.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.payment.TossPayment;

public interface TossPaymentRepository extends JpaRepository<TossPayment, Long> {

}
