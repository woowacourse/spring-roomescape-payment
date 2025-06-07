package roomescape.payment.repository;

import java.util.Optional;
import roomescape.payment.domain.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findByPaymentKey(String s);

    Optional<Payment> findByReservationId(Long id);
}
