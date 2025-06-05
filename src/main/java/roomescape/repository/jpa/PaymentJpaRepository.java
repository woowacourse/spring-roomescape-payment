package roomescape.repository.jpa;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.payment.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    boolean existsPaymentByReservationId(Long reservationId);

    Optional<Payment> findPaymentByReservationId(Long reservationId);
}
