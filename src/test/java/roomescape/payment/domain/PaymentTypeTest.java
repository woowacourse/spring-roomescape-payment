package roomescape.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("결제 타입 도메인 테스트")
class PaymentTypeTest {

    @DisplayName("결제 타입이 존재하지 않을 경우, 예외가 발생한다.")
    @Test
    void from() {
        //given
        String invalidType = "카카드";

        //when & then
        assertThatThrownBy(() -> PaymentType.from(invalidType))
                .isInstanceOf(IllegalArgumentException.class);
    }
}