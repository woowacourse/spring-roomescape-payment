package roomescape.payment.pg;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import roomescape.global.exception.ViolationException;
import roomescape.payment.application.PaymentConfirmRequest;
import roomescape.payment.pg.TossPaymentsConfirmRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TossPaymentsConfirmRequestTest {

    @Test
    @DisplayName("paymentKey의 길이는 최대 200자이다.")
    void validatePaymentKeySize() {
        PaymentConfirmRequest createRequest = createPaymentConfirmRequest("*".repeat(201), "orderId");

        assertThatThrownBy(() -> new TossPaymentsConfirmRequest(createRequest))
                .isInstanceOf(ViolationException.class)
                .hasMessage("paymentKey는 최대 200자입니다.");
    }

    @Test
    @DisplayName("orderId는 6자 이상이어야 한다.")
    void validateOrderIdMinLength() {
        PaymentConfirmRequest createRequest = createPaymentConfirmRequest("*".repeat(200), "*".repeat(5));

        assertThatThrownBy(() -> new TossPaymentsConfirmRequest(createRequest))
                .isInstanceOf(ViolationException.class)
                .hasMessage("orderId는 6자 이상 64자 이하의 문자열입니다.");
    }

    @Test
    @DisplayName("orderId는 64자 이하이어야 한다.")
    void validateOrderIdMaxLength() {
        PaymentConfirmRequest createRequest = createPaymentConfirmRequest("*".repeat(200), "*".repeat(65));

        assertThatThrownBy(() -> new TossPaymentsConfirmRequest(createRequest))
                .isInstanceOf(ViolationException.class)
                .hasMessage("orderId는 6자 이상 64자 이하의 문자열입니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"orderId-_123_AZ", "AzAzAz", "valid_order_id"})
    @DisplayName("orderId는 영문 대소문자, 숫자, 특수문자 -, _로 이루어져 있어야 한다.")
    void validateOrderIdFormatHappy(String happySource) {
        PaymentConfirmRequest createRequest = createPaymentConfirmRequest("*".repeat(200), happySource);

        assertThatCode(() -> new TossPaymentsConfirmRequest(createRequest))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"order#id123", "order id", "!@order_id"})
    @DisplayName("orderId는 영문 대소문자, 숫자, 특수문자 -, _로 이루어져 있어야 한다.")
    void validateOrderIdFormatException(String exceptionSource) {
        PaymentConfirmRequest createRequest = createPaymentConfirmRequest("*".repeat(200), exceptionSource);

        assertThatThrownBy(() -> new TossPaymentsConfirmRequest(createRequest))
                .isInstanceOf(ViolationException.class)
                .hasMessage("orderId는 영문 대소문자, 숫자, 특수문자 -, _로 이루어져야 합니다.");
    }

    private PaymentConfirmRequest createPaymentConfirmRequest(String paymentKey, String orderId) {
        return new PaymentConfirmRequest(paymentKey, orderId, BigDecimal.valueOf(1000L), "card");
    }
}
