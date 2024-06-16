package roomescape.payment.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import roomescape.exception.BadArgumentRequestException;

class PaymentKeyTest {
    @DisplayName("결제 키가 null일 경우 예외를 던진다.")
    @Test
    void validateTest_whenValueIsNull() {
        assertThatThrownBy(() -> new PaymentKey(null))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("결제 키가 비어있을 경우 예외를 던진다.")
    @ParameterizedTest
    @CsvSource({"''", "'  '", "'\t\n'"})
    void validateTest_whenValueIsEmpty(String emptyKey) {
        assertThatThrownBy(() -> new PaymentKey(emptyKey))
                .isInstanceOf(BadArgumentRequestException.class)
                .hasMessage("결제 키는 1글자 이상 50글자 이하이어야 합니다.");
    }

    @DisplayName("결제 키가 50자 초과일 경우 예외를 던진다.")
    @Test
    void validateTest_whenValueIsLong() {
        assertThatThrownBy(() -> new PaymentKey("a".repeat(51)))
                .isInstanceOf(BadArgumentRequestException.class)
                .hasMessage("결제 키는 1글자 이상 50글자 이하이어야 합니다.");
    }
}
