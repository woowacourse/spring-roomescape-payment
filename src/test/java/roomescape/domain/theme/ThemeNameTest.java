package roomescape.domain.theme;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import roomescape.BaseTest;
import roomescape.exception.RoomEscapeException;

class ThemeNameTest extends BaseTest {

    @Test
    void 테마명이_비어있을_경우_예외_발생() {
        // given
        String themeName = "";

        // when, then
        assertThatThrownBy(() -> new ThemeName(themeName))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 테마명이_20자_초과인_경우_예외_발생() {
        // given
        String themeName = "123456789012345678901";

        // when, then
        assertThatThrownBy(() -> new ThemeName(themeName))
                .isInstanceOf(RoomEscapeException.class);
    }
}
