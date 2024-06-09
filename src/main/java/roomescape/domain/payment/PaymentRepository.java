package roomescape.domain.payment;

import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findByOrderId(String orderId);

    Payment getByOrderId(String orderId);

    boolean updateStatus(String orderId, String paymentKey, PaymentStatus status);
}
