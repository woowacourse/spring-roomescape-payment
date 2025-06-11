package roomescape.payment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.ProductType;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByPaymentKey(String paymentKey);

    Payment findByProductTypeAndProductId(ProductType productType, Long productId);
}
