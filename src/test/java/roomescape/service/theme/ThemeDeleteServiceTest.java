package roomescape.service.theme;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.theme.Theme;
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

        // then
        List<Theme> allThemes = themeRepository.findAll();
        assertThat(allThemes).extracting(Theme::getId)
                .isNotEmpty()
                .doesNotContain(1L);
    }
}
