package roomescape.payment.infrastructure;

import java.util.Optional;
import roomescape.payment.domain.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);

    void deleteById(Long id);

    Optional<Payment> findById(Long id);
}
