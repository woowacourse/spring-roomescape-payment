package roomescape.domain.reservation.pendingpayment;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingPaymentRepository extends JpaRepository<PendingPayment, Long> {

    @EntityGraph(attributePaths = {"user", "theme", "timeSlot"})
    List<PendingPayment> findByUserId(Long userId);
}
