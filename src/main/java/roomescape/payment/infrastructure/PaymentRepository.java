package roomescape.payment.infrastructure;

import roomescape.payment.domain.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);

    void deleteById(Long id);
}
