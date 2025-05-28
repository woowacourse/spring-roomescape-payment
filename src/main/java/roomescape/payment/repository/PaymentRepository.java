package roomescape.payment.repository;

import org.springframework.stereotype.Repository;
import roomescape.payment.domain.Payment;

@Repository
public interface PaymentRepository {

    Payment save(final Payment payment);
}
