package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.theme.ThemeName;

@Sql("/test-data.sql")
class ThemeRepositoryTest extends RepositoryBaseTest{

    @Autowired
    ThemeRepository themeRepository;

    @Test
    void 주어진_테마_이름으로_등록된_테마가_있는지_확인() {
        // when
        boolean result = themeRepository.existsByThemeName(new ThemeName("테마1"));

        // then
        assertThat(result).isTrue();
    }
}
