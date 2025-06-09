package roomescape.payment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import roomescape.payment.exception.InvalidPaymentException;

class AmountTest {

    @Test
    @DisplayName("유효한 금액으로 Amount를 생성한다.")
    void createAmount_whenValidRequest_returnAmount() {
        // given
        Long validAmount = 10000L;

        // when
        Amount amount = Amount.from(validAmount);

        // then
        assertThat(amount.value()).isEqualTo(validAmount);
    }

    @Test
    @DisplayName("금액이 null이면 예외가 발생한다.")
    void createAmount_whenNull_throwsException() {
        // when & then
        assertThatThrownBy(() -> Amount.from(null))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessage("결제 금액은 null일 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -1000L})
    @DisplayName("금액이 0 이하면 예외가 발생한다.")
    void createAmount_whenZeroOrNegative_throwsException(Long invalidAmount) {
        // when & then
        assertThatThrownBy(() -> Amount.from(invalidAmount))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessage("결제 금액은 0보다 커야 합니다.");
    }
} 