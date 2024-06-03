package roomescape.infrastructure.payment;

import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;

public interface PaymentJpaRepository extends
        PaymentRepository,
        ListCrudRepository<Payment, String> {

    Optional<Payment> findByOrderId(String orderId);

    @Override
    default Payment getByOrderId(String orderId) {
        return findByOrderId(orderId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 결제 정보입니다."));
    }
}
