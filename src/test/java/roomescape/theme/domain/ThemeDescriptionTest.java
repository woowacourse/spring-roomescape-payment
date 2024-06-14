package roomescape.theme.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.advice.exception.RoomEscapeException;

class ThemeDescriptionTest {
    @DisplayName("테마 설명이 null일 경우 예외를 던진다.")
    @Test
    void validateTest_whenValueIsNull() {
        assertThatThrownBy(() -> new ThemeDescription(null))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("테마 설명은 null 일 수 없습니다.");
    }

    @DisplayName("테마 설명이 1글자 미만일 경우 예외를 던진다.")
    @Test
    void validateTest_whenValueIsEmpty() {
        assertThatThrownBy(() -> new ThemeDescription(""))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("테마 설명은 1글자 이상 255글자 이하이어야 합니다.");
    }

    @DisplayName("테마 설명이 255글자 초과일 경우 예외를 던진다.")
    @Test
    void validateTest_whenValueIsLong() {
        assertThatThrownBy(() -> new ThemeDescription("a".repeat(256)))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("테마 설명은 1글자 이상 255글자 이하이어야 합니다.");
    }
}
