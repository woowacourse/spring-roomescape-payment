package roomescape.domain.repository;

import roomescape.domain.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);
}
