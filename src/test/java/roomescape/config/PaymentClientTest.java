package roomescape.config;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import roomescape.dto.PaymentRequest;
import roomescape.exception.RoomescapeException;

@SpringBootTest
class PaymentClientTest {

    @Autowired
    PaymentClient paymentClient;

    @DisplayName("외부 API와 연동할 수 있다.")
    @Test
    void approve() {
        assertThatThrownBy(() -> paymentClient.approve(new PaymentRequest("invalidPaymentKey", "123", 1000)))
                .isInstanceOf(RoomescapeException.class);
    }
}
