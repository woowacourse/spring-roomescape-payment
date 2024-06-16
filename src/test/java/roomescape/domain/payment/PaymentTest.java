package roomescape.domain.payment;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PaymentTest {

    @DisplayName("결제 금액이 0 초과면 정상 생성된다.")
    @Test
    void throw_exception_when_amount_greater_than_0() {
        String dateTime = LocalDateTime.now().toString();

        assertThatNoException()
                .isThrownBy(() -> new Payment(BigDecimal.valueOf(0.1), "paymentKey", "orderId", dateTime, dateTime));
    }

    @ParameterizedTest
    @DisplayName("결제 금액이 0이하면 예외를 발생시킨다.")
    @ValueSource(doubles = {-1000, -0.1, 0})
    void throw_exception_when_amount_less_than_0(double amount) {
        String dateTime = LocalDateTime.now().toString();

        assertThatThrownBy(
                () -> new Payment(BigDecimal.valueOf(amount), "paymentKey", "orderId", dateTime, dateTime)
        );
    }
}
