package roomescape.domain.theme;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import roomescape.exception.custom.RoomEscapeException;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ThemeNameTest {

    @Test
    void 테마명이_비어있을_경우_예외_발생() {
        //given
        String themeName = "";

        //when, then
        assertThatThrownBy(() -> new ThemeName(themeName))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 테마명이_20자_초과인_경우_예외_발생() {
        //given
        String themeName = "123456789012345678901";

        //when, then
        assertThatThrownBy(() -> new ThemeName(themeName))
                .isInstanceOf(RoomEscapeException.class);
    }
}
