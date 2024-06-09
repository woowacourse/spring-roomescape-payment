package roomescape.paymenthistory.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AmountTest {

    @ParameterizedTest
    @ValueSource(longs = {10000, 20000, 0, 140000})
    @DisplayName("방탈출의 가격이 맞지 않는 경우 에러를 던진다.")
    void validation(long amount) {
        assertThatThrownBy(() -> new Amount(amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("방탈출 가격이 일치하지 않습니다.");
    }
}
