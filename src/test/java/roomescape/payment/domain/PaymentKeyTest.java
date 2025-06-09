package roomescape.payment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import roomescape.payment.exception.InvalidPaymentException;

class PaymentKeyTest {

    @Test
    @DisplayName("유효한 결제 키로 PaymentKey를 생성한다.")
    void createPaymentKey_whenValidRequest_returnPaymentKey() {
        // given
        String validPaymentKey = "payment_key_123";

        // when
        PaymentKey paymentKey = PaymentKey.from(validPaymentKey);

        // then
        assertThat(paymentKey.value()).isEqualTo(validPaymentKey);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("결제 키가 null이거나 비어있으면 예외가 발생한다.")
    void createPaymentKey_whenInvalidInput_throwsException(String invalidPaymentKey) {
        // when & then
        assertThatThrownBy(() -> PaymentKey.from(invalidPaymentKey))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessage("결제 키는 비어있을 수 없습니다.");
    }
} 
