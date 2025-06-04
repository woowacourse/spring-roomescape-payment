package roomescape.domain.payment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import roomescape.exception.BusinessRuleViolationException;

class OrderIdTest {

    @Test
    @DisplayName("주문번호가 6자 미만이면 예외가 발생한다.")
    void orderIdLengthCannotShorterThanMin() {
        assertThatThrownBy(() -> new OrderId("order"))
            .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    @DisplayName("주문번호가 64자를 초과하면 예외가 발생한다.")
    void orderIdLengthCannotOverMax() {
        assertThatThrownBy(() -> new OrderId("a".repeat(65)))
            .isInstanceOf(BusinessRuleViolationException.class);
    }

    @ParameterizedTest
    @DisplayName("주문번호 형식이 맞지 않으면 예외가 발생한다.")
    @ValueSource(strings = {"order Id", "order@.", " 1234_-", "orderId가"})
    void orderIdFormatShouldValid(final String invalidId) {
        assertThatThrownBy(() -> new OrderId(invalidId))
            .isInstanceOf(BusinessRuleViolationException.class);
    }
}
