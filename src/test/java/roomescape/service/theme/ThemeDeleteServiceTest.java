package roomescape.service.theme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.theme.Theme;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ThemeRepository;
import roomescape.service.ServiceBaseTest;

class ThemeDeleteServiceTest extends ServiceBaseTest {

    @Autowired
    ThemeDeleteService themeDeleteService;

    @Autowired
    ThemeRepository themeRepository;

    @Sql("/reset-data.sql")
    @Test
    void 테마_삭제() {
        // given
        themeRepository.save(new Theme("테마1", "설명 설명 설명 설명", "썸네일"));
        themeRepository.save(new Theme("테마2", "설명 설명 설명 설명", "썸네일"));

        // when
        themeDeleteService.deleteTheme(1L);

        // when
        List<Theme> allThemes = themeRepository.findAll();
        assertThat(allThemes).extracting(Theme::getId)
                .isNotEmpty()
                .doesNotContain(1L);
    }

    @Test
    void 예약_되어있는_테마를_삭제할_경우_예외_발생() {
        // then
        assertThatThrownBy(() -> themeDeleteService.deleteTheme(1L))
                .isInstanceOf(RoomEscapeException.class);
    }
}
