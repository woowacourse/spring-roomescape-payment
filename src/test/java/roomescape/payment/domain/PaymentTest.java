package roomescape.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.common.validate.InvalidInputException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Payment 도메인 테스트")
class PaymentTest {

    private static final String VALID_PAYMENT_KEY = "test_payment_key_123";
    private static final PaymentAmount VALID_AMOUNT = PaymentAmount.from(10000);
    private static final String VALID_ORDER_ID = "order_123456";
    private static final String VALID_PAYMENT_TYPE = "CARD";

    @Test
    @DisplayName("Payment Key가 null이면 예외가 발생한다")
    void throwExceptionWhenPaymentKeyIsNull() {
        // when & then
        assertThatThrownBy(() -> Payment.of(null, VALID_AMOUNT, VALID_ORDER_ID, VALID_PAYMENT_TYPE))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Validation failed [while checking null]: Payment.paymentKey");
    }

    @Test
    @DisplayName("Order ID가 null이면 예외가 발생한다")
    void throwExceptionWhenOrderIdIsNull() {
        // when & then
        assertThatThrownBy(() -> Payment.of(VALID_PAYMENT_KEY, VALID_AMOUNT, null, VALID_PAYMENT_TYPE))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Validation failed [while checking null]: Payment.orderId");
        ;
    }

    @Test
    @DisplayName("Payment Type가 null이면 예외가 발생한다")
    void throwExceptionWhenPaymentTypeIsNull() {
        // when & then
        assertThatThrownBy(() -> Payment.of(VALID_PAYMENT_KEY, VALID_AMOUNT, VALID_ORDER_ID, null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Validation failed [while checking null]: Payment.paymentType");
    }

    @Test
    @DisplayName("amount가 null이면 예외가 발생한다")
    void throwExceptionWhenPaymentAmountIsNull() {
        // when & then
        assertThatThrownBy(() -> Payment.of(VALID_PAYMENT_KEY, null, VALID_ORDER_ID, VALID_PAYMENT_TYPE))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Validation failed [while checking null]: Payment.amount");
    }
}
