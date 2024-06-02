package roomescape.domain.payment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByMemberIdAndStatus(Long memberId, PaymentStatus status);
}
