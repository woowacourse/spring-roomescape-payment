package roomescape.service.theme.module;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeName;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ThemeRepository;
import roomescape.service.ServiceBaseTest;

class ThemeValidatorTest extends ServiceBaseTest {

    @Autowired
    ThemeValidator themeValidator;

    @Autowired
    ThemeRepository themeRepository;

    @Test
    void 중복된_테마명일_경우_예외_발생() {
        //given
        ThemeName themeName = new ThemeName("테마1");

        //when, then
        assertThatThrownBy(() -> themeValidator.validateNameDuplicate(themeName))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 예약_되어있는_테마를_삭제할_경우_예외_발생() {
        // given
        Theme theme = themeRepository.findByIdOrThrow(1L);

        // when, then
        assertThatThrownBy(() -> themeValidator.validateDeletable(theme))
                .isInstanceOf(RoomEscapeException.class);
    }
}
