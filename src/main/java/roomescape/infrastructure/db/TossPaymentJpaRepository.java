package roomescape.infrastructure.db;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.model.TossPayment;

public interface TossPaymentJpaRepository extends JpaRepository<TossPayment, Long> {
    Optional<TossPayment> findByReservationTicketId(Long id);
}
