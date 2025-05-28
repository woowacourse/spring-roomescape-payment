package roomescape.payment.domain;

import java.util.Optional;

public interface PrePaymentRepository {
    Optional<PrePayment> findByOrderId(String orderId);

    void save(PrePayment prePayment);
}
