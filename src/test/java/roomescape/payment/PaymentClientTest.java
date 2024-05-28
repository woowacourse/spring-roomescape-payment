package roomescape.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.IntegrationTestSupport;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.exception.PaymentException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentClientTest extends IntegrationTestSupport {

    @Autowired
    private PaymentClient paymentClient;

    @Test
    @DisplayName("유효하지 않은 키로 결제 시도하면 실패한다.")
    void postPaymentDuplicateFailTest() {
        final PaymentRequest paymentRequest = new PaymentRequest("invalidPaymentKey", "MC40ODQxNTkyMjA5Mjcx", 10011L);

        assertThatThrownBy(() -> paymentClient.postPayment(paymentRequest))
                .isInstanceOf(PaymentException.class);
    }
}
