package roomescape.payment.repository;

import roomescape.payment.domain.Payment;

public interface PaymentRepositoryInterface {

    Payment save(final Payment payment);
}
