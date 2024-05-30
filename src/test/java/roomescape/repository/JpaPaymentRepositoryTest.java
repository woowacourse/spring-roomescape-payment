package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Payment;

@SpringBootTest
@Transactional
class JpaPaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("Payment 를 잘 저장하는지 확인한다.")
    void save() {
        Payment saved = paymentRepository.save(new Payment(null, "paymentKey1", "WTEST12345", 1000L));

        assertThat(saved.getId()).isNotNull();
    }
}
