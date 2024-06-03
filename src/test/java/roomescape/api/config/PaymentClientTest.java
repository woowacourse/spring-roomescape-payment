package roomescape.api.config;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import roomescape.exception.PaymentException;
import roomescape.payment.config.PaymentClient;
import roomescape.payment.dto.PaymentRequest;

@SpringBootTest
class PaymentClientTest {

    @Autowired
    PaymentClient paymentClient;

    @Disabled
    @DisplayName("실제 api를 연동하여 paymentkey를 클라이언트에서 획득하지 않은 값으로 요청한 경우 예외가 발생한다.")
    @Test
    void payment() {
        assertThatThrownBy(() -> paymentClient.payment(new PaymentRequest("invalidPaymentKey", "-1", 1000)))
                .isInstanceOf(PaymentException.class);
    }
}
