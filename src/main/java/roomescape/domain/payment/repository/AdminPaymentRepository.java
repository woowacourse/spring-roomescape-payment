package roomescape.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.payment.AdminPayment;

import java.util.List;

public interface AdminPaymentRepository extends JpaRepository<AdminPayment, Long> {

    List<AdminPayment> findAllByPaymentIdIn(List<Long> paymentIds);
}
