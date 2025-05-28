package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import roomescape.reservation.infrastructure.PaymentClient;
import roomescape.reservation.infrastructure.TossPaymentClient;
import roomescape.reservation.presentation.dto.PaymentRequest;
import roomescape.reservation.service.PaymentServiceTest.PaymentConfig;

@SpringBootTest
@Import(PaymentConfig.class)
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @DisplayName("결제 요청을 승인한다")
    @Test
    void confirmPayment() {
        final String paymentKey = "tgen_20240513184816ZSAZ9";
        final String orderId = "MC4wNDYzMzA0OTc2MDgy";
        final int amount = 1000;
        final PaymentRequest request = new PaymentRequest(paymentKey, orderId, amount);

        assertThatCode(() -> paymentService.confirmPayment(request))
                .doesNotThrowAnyException();
    }

    @DisplayName("유효하지 않은 paymentKey인 경우 예외가 발생한다.")
    @Test
    void confirmPayment_shouldThrowException_invalidPaymentKey() {
        final String paymentKey = "tgen_20240513184816ZSAZ0";
        final String orderId = "MC4wNDYzMzA0OTc2MDgy";
        final int amount = 1000;
        final PaymentRequest request = new PaymentRequest(paymentKey, orderId, amount);

        assertThatThrownBy(() -> paymentService.confirmPayment(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    static class PaymentConfig {

        @Bean
        public PaymentClient paymentClient() {
            return new TossPaymentClient();
        }

        @Bean
        public PaymentService paymentService() {
            return new PaymentService(paymentClient());
        }
    }
}
