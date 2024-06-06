package roomescape.payment.pg;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TossPaymentsPaymentTest {

    @Test
    @DisplayName("confirmRequest 와 값이 일치하는지 확인할 수 있다.")
    void verify() {
        TossPaymentsConfirmRequest request =
                new TossPaymentsConfirmRequest("paymentKey", "orderId", BigDecimal.valueOf(1000L));
        TossPaymentsPayment target =
                new TossPaymentsPayment("paymentKey", "orderId", "DONE", BigDecimal.valueOf(1000L));

        boolean result = target.verify(request);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("confirmRequest 와 값이 일치하지 않는지 확인할 수 있다.")
    void noVerify() {
        TossPaymentsConfirmRequest request =
                new TossPaymentsConfirmRequest("paymentKey", "orderId", BigDecimal.valueOf(1000L));
        TossPaymentsPayment target =
                new TossPaymentsPayment("otherKey", "orderId", "DONE", BigDecimal.valueOf(1000L));

        boolean result = target.verify(request);

        assertThat(result).isFalse();
    }
}
