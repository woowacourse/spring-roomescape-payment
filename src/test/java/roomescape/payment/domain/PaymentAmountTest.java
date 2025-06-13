package roomescape.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.common.validate.InvalidInputException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentAmountTest {

    @Test
    @DisplayName("결제 금액이 음수이면 예외가 발생한다")
    void validateNonNegativePaymentAmount() {
        // when
        // then
        assertThatThrownBy(() -> PaymentAmount.from(-1))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Validation failed [while checking non-negative value]: PaymentAmount.value");
    }
}
