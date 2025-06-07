package roomescape.theme.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.theme.domain.Theme;

@DataJpaTest
class ThemeJpaRepositoryTest {

    @Autowired
    ThemeJpaRepository themeJpaRepository;

    @Test
    void 예약_빈도수에_따라_인기_테마를_조회할_수_있다() {
        LocalDate start = LocalDate.of(2025, 4, 15);
        LocalDate end = LocalDate.of(2025, 4, 30);

        List<Theme> popularThemes = themeJpaRepository.findPopularThemes(start, end);

        assertThat(popularThemes)
                .extracting(Theme::getName)
                .containsExactly("테마1", "테마2", "테마3"); // 테마1(2회), 테마3(1회), 테마2(1회) => 정렬 기준은 COUNT DESC
    }
}
