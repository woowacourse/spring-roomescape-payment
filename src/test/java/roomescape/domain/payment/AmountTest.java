package roomescape.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AmountTest {

    @Test
    @DisplayName("금액이 0 이상이면 Amount 객체가 생성된다.")
    void createAmount() {
        BigDecimal amount = BigDecimal.ZERO;

        Amount actual = new Amount(amount);

        assertThat(actual).isNotNull();
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("금액이 null이면 예외가 발생한다.")
    void validateAmountNull(BigDecimal amount) {
        assertThatThrownBy(() -> new Amount(amount))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("금액은 0 이상이어야 합니다.");
    }

    @Test
    @DisplayName("금액이 음수이면 예외가 발생한다.")
    void validateAmountNegative() {
        BigDecimal amount = BigDecimal.valueOf(-1);

        assertThatThrownBy(() -> new Amount(amount))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("금액은 0 이상이어야 합니다.");
    }
}
