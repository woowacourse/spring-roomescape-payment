package roomescape.infrastructure.payment;

import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.payment.PaymentStatus;

public interface PaymentJpaRepository extends
        PaymentRepository,
        ListCrudRepository<Payment, String> {

    Optional<Payment> findByOrderId(String orderId);

    @Override
    default Payment getByOrderId(String orderId) {
        return findByOrderId(orderId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 결제 정보입니다."));
    }

    @Override
    @Transactional
    default boolean updateStatus(String orderId, String paymentKey, PaymentStatus status) {
        return updateStatusByOrderId(orderId, paymentKey, status) > 0;
    }

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Payment p SET p.paymentKey = :paymentKey, p.status = :status WHERE p.orderId = :orderId")
    int updateStatusByOrderId(String orderId, String paymentKey, PaymentStatus status);
}
