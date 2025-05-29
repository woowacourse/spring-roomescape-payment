package roomescape.payment.repository;

import roomescape.payment.domain.Payment;

public interface PaymentRepository {

    Payment save(final Payment payment);
}
