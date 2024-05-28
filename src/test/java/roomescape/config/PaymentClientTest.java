package roomescape.config;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import roomescape.dto.PaymentRequest;
import roomescape.exception.PaymentException;

@SpringBootTest
class PaymentClientTest {

    @Autowired
    PaymentClient paymentClient;

    @DisplayName("paymentkey를 클라이언트에서 획득하지 않은 값으로 요청한 경우 예외가 발생한다.")
    @Test
    void approve() {
        assertThatThrownBy(() -> paymentClient.approve(new PaymentRequest("invalidPaymentKey", "-1", 1000)))
                .isInstanceOf(PaymentException.class);
    }
}
