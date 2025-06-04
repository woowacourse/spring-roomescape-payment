package roomescape.unit.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.business.model.entity.Payment;
import roomescape.infrastructure.PaymentRepository;

@DataJpaTest
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void orderId로_결제를_조회한다() {
        // given
        Payment payment = Payment.create("orderId1", 1000L);
        entityManager.persist(payment);
        // when
        Optional<Payment> optionalPayment = paymentRepository.findByOrderId("orderId1");
        // then
        assertThat(optionalPayment).isPresent();
    }

    @Test
    void orderId로_결제가_존재하는지_조회한다() {
        // given
        Payment payment = Payment.create("orderId1", 1000L);
        entityManager.persist(payment);
        // when
        boolean exist = paymentRepository.existsByOrderId("orderId1");
        // then
        assertThat(exist).isTrue();
    }
}
