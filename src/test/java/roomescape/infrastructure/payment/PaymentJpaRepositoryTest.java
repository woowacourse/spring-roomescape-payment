package roomescape.infrastructure.payment;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentStatus;

@DataJpaTest
class PaymentJpaRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PaymentJpaRepository paymentRepository;

    @Test
    @DisplayName("결제 정보를 업데이트한다.")
    void updateStatus() {
        entityManager.persist(new Payment("orderId", "paymentKey", 1000));
        boolean updated = paymentRepository.updateStatus("orderId", "paymentKey", PaymentStatus.SUCCESS);
        Payment payment = paymentRepository.getByOrderId("orderId");
        assertAll(
                () -> assertTrue(updated),
                () -> assertTrue(payment.isPurchased())
        );
    }
}
