package roomescape.domain.reservation;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ThemeTest {

    @Test
    @DisplayName("테마 설명이 200자를 초과하면 예외가 발생한다")
    void descriptionLengthTest() {
        String description = ".".repeat(201);
        assertThatCode(() -> new Theme("테마명", description, 10_000L, "url"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("테마 설명은 200자 이하여야 합니다.");
    }

    @Test
    @DisplayName("테마 썸네일 URL이 200자를 초과하면 예외가 발생한다")
    void urlLengthTest() {
        String url = "a".repeat(201);
        assertThatCode(() -> new Theme("테마명", "description", 10_000L, url))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("테마 썸네일 URL은 200자 이하여야 합니다.");
    }

    @Test
    @DisplayName("테마 가격이 음수인 경우 예외가 발생한다.")
    void negativePriceTest() {
        assertThatCode(() -> new Theme("테마명", "description", -1_000L, "url"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가격은 0원 이상이어야 합니다.");
    }
}
