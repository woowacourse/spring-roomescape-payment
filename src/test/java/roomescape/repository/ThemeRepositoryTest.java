package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.theme.ThemeName;
import roomescape.exception.RoomEscapeException;

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

    @Test
    void 존재하지_않는_id로_조회시_예외_발생() {
        // when, then
        assertThatThrownBy(() -> themeRepository.findByIdOrThrow(1000L))
                .isInstanceOf(RoomEscapeException.class);
    }
}
