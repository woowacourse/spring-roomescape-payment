package roomescape.payment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByMemberId(Long memberId);
}
