package roomescape.payment.model.repository;

import roomescape.payment.model.entity.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);
}
