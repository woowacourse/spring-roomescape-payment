package roomescape.theme.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.advice.exception.RoomEscapeException;

class ThemeTest {
    @DisplayName("이름이 비어있을 때 예외를 던진다.")
    @Test
    void validateThemeTest_whenNameIsNull() {
        assertThatThrownBy(() ->
                new Theme(1L, null, "오리들과 호랑이들 사이에서 살아남기", "https://image.jpg"))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("테마 이름은 null 일 수 없습니다.");
    }

    @DisplayName("설명이 비어있을 때 예외를 던진다.")
    @Test
    void validateThemeTest_whenDescriptionIsNull() {
        assertThatThrownBy(() ->
                new Theme(1L, "오리와 호랑이", null, "https://image.jpg"))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("테마 설명은 null 일 수 없습니다.");
    }


    @DisplayName("썸네일이 비어있을 때 예외를 던진다.")
    @Test
    void validateThemeTest_whenThumbnailIsNull() {
        assertThatThrownBy(() ->
                new Theme(1L, "오리와 호랑이", "오리들과 호랑이들 사이에서 살아남기", null))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("테마 썸네일은 null 일 수 없습니다.");
    }
}
