package roomescape.payment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentKey(String paymentKey);

    Optional<Payment> findByReservationId(Long reservationId);

    default Payment fetchByPaymentKey(String paymentKey) {
        return findByPaymentKey(paymentKey).orElseThrow(RuntimeException::new);
    }
}
