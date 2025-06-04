package roomescape.business.model.repository;

import roomescape.business.model.entity.Payment;

public interface PaymentRepository {
    void save(Payment payment);
}
