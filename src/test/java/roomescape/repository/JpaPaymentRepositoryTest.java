package roomescape.repository;

import static roomescape.fixture.PaymentFixture.DEFAULT_PAYMENT_WITHOUT_ID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class JpaPaymentRepositoryTest extends DatabaseClearBeforeEachTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("Payment를 잘 저장하는지 확인한다.")
    void save() {
        var saved = paymentRepository.save(DEFAULT_PAYMENT_WITHOUT_ID);

        Assertions.assertThat(saved.getId())
                .isNotNull();
    }
}
