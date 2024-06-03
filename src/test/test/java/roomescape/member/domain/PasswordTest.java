package roomescape.member.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.exception.BadArgumentRequestException;

class PasswordTest {
    @DisplayName("예약자 비밀번호가 null일 경우 예외를 던진다.")
    @Test
    void validateTest_whenValueIsNull() {
        assertThatThrownBy(() -> new Password(null))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("예약자 비밀번호가 1자 미만일 경우 예외를 던진다.")
    @Test
    void validateTest_whenValueIsEmpty() {
        assertThatThrownBy(() -> new Password(""))
                .isInstanceOf(BadArgumentRequestException.class)
                .hasMessage("예약자 비밀번호는 1글자 이상 100글자 이하이어야 합니다.");
    }

    @DisplayName("예약자 비밀번호가 100자 초과일 경우 예외를 던진다.")
    @Test
    void validateTest_whenValueIsLong() {
        assertThatThrownBy(() -> new Password("a".repeat(101)))
                .isInstanceOf(BadArgumentRequestException.class)
                .hasMessage("예약자 비밀번호는 1글자 이상 100글자 이하이어야 합니다.");
    }
}
