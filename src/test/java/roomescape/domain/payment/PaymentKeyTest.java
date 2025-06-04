package roomescape.domain.payment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.exception.BusinessRuleViolationException;

class PaymentKeyTest {

    @Test
    @DisplayName("결제 키값이 공백이면 예외가 발생한다.")
    void paymentKeyCannotContainSpace() {
        assertThatThrownBy(() -> new PaymentKey(" "))
            .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    @DisplayName("결제 키값이 200자를 초과하면 예외가 발생한다.")
    void paymentKeyLengthCannotOverMax() {
        assertThatThrownBy(() -> new PaymentKey("가".repeat(201)))
            .isInstanceOf(BusinessRuleViolationException.class);
    }
}
