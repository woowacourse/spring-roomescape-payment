package roomescape.payment.repository;

import roomescape.payment.domain.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);
}
