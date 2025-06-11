package roomescape.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.payment.TossPayment;

import java.util.List;

public interface TossPaymentRepository extends JpaRepository<TossPayment, Long> {

    List<TossPayment> findAllByPaymentIdIn(List<Long> paymentIds);
}
