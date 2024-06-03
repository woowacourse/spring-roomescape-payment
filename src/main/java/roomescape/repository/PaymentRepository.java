package roomescape.repository;

import roomescape.domain.payment.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);
}
