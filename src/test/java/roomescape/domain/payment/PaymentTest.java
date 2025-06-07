package roomescape.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import roomescape.exception.BusinessRuleViolationException;
import roomescape.exception.InvalidInputException;

class PaymentTest {

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("결제 요청 번호가 null이거나 공백일 경우 예외를 던진다.")
    void validatePaymentKey_WhenNullOrEmpty(final String paymentKey) {
        // given
        String orderId = "orderId";
        String orderName = "orderName";
        Long amount = 1000L;

        // when & then
        assertThatThrownBy(() -> Payment.register(paymentKey, orderId, orderName, amount))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("결제 요청 번호는 null이거나 공백일 수 없습니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("주문 번호가 null이거나 공백일 경우 예외를 던진다.")
    void validateOrderId_WhenNullOrEmpty(final String orderId) {
        // given
        String paymentKey = "paymentKey";
        String orderName = "orderName";
        Long amount = 1000L;

        // when & then
        assertThatThrownBy(() -> Payment.register(paymentKey, orderId, orderName, amount))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("주문 번호는 null이거나 공백일 수 없습니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("주문 이름이 null이거나 공백일 경우 예외를 던진다.")
    void validateOrderName_WhenNullOrEmpty(final String orderName) {
        // given
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 1000L;

        // when & then
        assertThatThrownBy(() -> Payment.register(paymentKey, orderId, orderName, amount))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("주문 이름은 null이거나 공백일 수 없습니다.");
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("결제 금액이 null일 경우 예외를 던진다.")
    void validateAmount_WhenNullOrEmpty(final Long amount) {
        // given
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        String orderName = "orderName";

        // when & then
        assertThatThrownBy(() -> Payment.register(paymentKey, orderId, orderName, amount))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("결제 금액은 null일 수 없습니다.");
    }

    @Test
    @DisplayName("결제 금액이 음수면 예외를 던진다.")
    void validateAmount_WhenNegativeNumber() {
        // given
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        String orderName = "orderName";
        Long amount = -300L;

        // when & then
        assertThatThrownBy(() -> Payment.register(paymentKey, orderId, orderName, amount))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("결제 금액은 음수일 수 없습니다.");
    }

    @Test
    @DisplayName("결제 상태 성공 변경 시, 결제 대기 상태가 아니면 예외를 던진다.")
    void completePayment_WhenStatusIsNotPending_ThenThrowException() {
        // given
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        String orderName = "orderName";
        Long amount = 1000L;

        Payment payment = Payment.register(paymentKey, orderId, orderName, amount);
        payment.rejectPayment();

        // when & then
        assertAll(
                () -> assertThat(payment.getStatus()).isNotEqualTo(PaymentStatus.PENDING),
                () -> assertThatThrownBy(payment::completePayment)
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("결제 대기 상태에서만 성공 상태로 변경할 수 있습니다.")
        );
    }

    @Test
    @DisplayName("결제 상태를 성공으로 변경한다.")
    void completePayment() {
        // given
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        String orderName = "orderName";
        Long amount = 1000L;

        Payment payment = Payment.register(paymentKey, orderId, orderName, amount);

        // when
        payment.completePayment();

        // then
        assertAll(
                () -> assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS),
                () -> assertThat(payment.getPaymentKey()).isEqualTo(paymentKey),
                () -> assertThat(payment.getOrderId()).isEqualTo(orderId),
                () -> assertThat(payment.getOrderName()).isEqualTo(orderName),
                () -> assertThat(payment.getAmount()).isEqualTo(amount)
        );
    }

    @Test
    @DisplayName("결제 상태 실패 변경 시, 결제 대기 상태가 아니면 예외를 던진다.")
    void rejectPayment_WhenStatusIsNotPending_ThenThrowException() {
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        String orderName = "orderName";
        Long amount = 1000L;

        Payment payment = Payment.register(paymentKey, orderId, orderName, amount);
        payment.completePayment();

        // when & then
        assertAll(
                () -> assertThat(payment.getStatus()).isNotEqualTo(PaymentStatus.PENDING),
                () -> assertThatThrownBy(payment::rejectPayment)
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("결제 대기 상태에서만 실패 상태로 변경할 수 있습니다.")
        );
    }

    @Test
    @DisplayName("결제 상태를 실패로 변경한다.")
    void rejectPayment() {
        // given
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        String orderName = "orderName";
        Long amount = 1000L;

        Payment payment = Payment.register(paymentKey, orderId, orderName, amount);

        // when
        payment.rejectPayment();

        // then
        assertAll(
                () -> assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED),
                () -> assertThat(payment.getPaymentKey()).isEqualTo(paymentKey),
                () -> assertThat(payment.getOrderId()).isEqualTo(orderId),
                () -> assertThat(payment.getOrderName()).isEqualTo(orderName),
                () -> assertThat(payment.getAmount()).isEqualTo(amount)
        );
    }

    @Test
    @DisplayName("결제 정보를 정상적으로 생성한다.")
    void register() {
        // given
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        String orderName = "orderName";
        Long amount = 1000L;

        // when
        Payment payment = Payment.register(paymentKey, orderId, orderName, amount);

        // then
        assertAll(
                () -> assertThat(payment).isNotNull(),
                () -> assertThat(payment.getPaymentKey()).isEqualTo(paymentKey),
                () -> assertThat(payment.getOrderId()).isEqualTo(orderId),
                () -> assertThat(payment.getOrderName()).isEqualTo(orderName),
                () -> assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING),
                () -> assertThat(payment.getAmount()).isEqualTo(amount)
        );
    }
}
