package roomescape.theme.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.common.exception.InvalidInputException;

import static org.assertj.core.api.SoftAssertions.*;

class ThemeTest {

    @Test
    @DisplayName("테마의 필수 필드가 null이라면 예외가 발생한다")
    void cannotNullTime() {
        // when & then
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> Theme.withoutId(
                    null,
                    ThemeDescription.from("테마 설명"),
                    ThemeThumbnail.from("테마 썸네일")
            )).isInstanceOf(InvalidInputException.class)
                    .hasMessage("Theme.name 은(는) null일 수 없습니다.");

            softAssertions.assertThatThrownBy(() -> Theme.withoutId(
                    ThemeName.from("테마 이름"),
                    null,
                    ThemeThumbnail.from("테마 썸네일")
            )).isInstanceOf(InvalidInputException.class)
                    .hasMessage("Theme.description 은(는) null일 수 없습니다.");

            softAssertions.assertThatThrownBy(() -> Theme.withoutId(
                    ThemeName.from("테마 이름"),
                    ThemeDescription.from("테마 설명"),
                    null
            )).isInstanceOf(InvalidInputException.class)
                    .hasMessage("Theme.thumbnail 은(는) null일 수 없습니다.");

        });
    }
}
