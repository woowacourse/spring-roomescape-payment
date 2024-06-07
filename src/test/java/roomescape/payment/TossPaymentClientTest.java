package roomescape.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import roomescape.payment.dto.CreatePaymentRequest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("test")
class TossPaymentClientTest {

    @Autowired
    PaymentClient paymentClient;

    @Test
    @DisplayName("유효하지 않은 결제 요청인 경우 예외가 발생한다.")
    void invalidPaymentRequest() {
        final CreatePaymentRequest paymentRequest = new CreatePaymentRequest("invalid", "invalid", 1L);
        assertThatThrownBy(() -> paymentClient.postPayment(paymentRequest))
                .isInstanceOf(TossPaymentException.class);
    }
}
