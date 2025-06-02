package roomescape.payment.service.client;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import roomescape.common.exception.PaymentException;
import roomescape.payment.service.dto.PaymentConfirmRequest;

@SpringBootTest
class TossPaymentsClientTest {

    @Autowired
    private TossPaymentsClient tossPaymentsClient;

    @Test
    @DisplayName("적절하지 않은 정보로 결제를 요청할 시 예외가 발생한다")
    void completePaymentFailed() {
        // Given
        PaymentConfirmRequest request = new PaymentConfirmRequest(
            "payment123",
            "order456",
            10000L
        );

        // When & Then
        assertThatThrownBy(() -> tossPaymentsClient.completePayment(request))
            .isInstanceOf(PaymentException.class);
    }
}
