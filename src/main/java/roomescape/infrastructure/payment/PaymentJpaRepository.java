package roomescape.infrastructure.payment;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long>, PaymentRepository {

    Optional<Payment> findByReservationId(Long id);
}
