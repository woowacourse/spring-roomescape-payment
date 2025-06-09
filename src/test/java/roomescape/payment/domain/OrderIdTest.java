package roomescape.payment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import roomescape.payment.exception.InvalidPaymentException;

class OrderIdTest {

    @Test
    @DisplayName("유효한 주문 ID로 OrderId를 생성한다.")
    void createOrderId_whenValidRequest_returnOrderId() {
        // given
        String validOrderId = "order_123";

        // when
        OrderId orderId = OrderId.from(validOrderId);

        // then
        assertThat(orderId.value()).isEqualTo(validOrderId);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("주문 ID가 null이거나 비어있으면 예외가 발생한다.")
    void createOrderId_whenInvalidInput_throwsException(String invalidOrderId) {
        // when & then
        assertThatThrownBy(() -> OrderId.from(invalidOrderId))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessage("주문 ID는 비어있을 수 없습니다.");
    }
} 
