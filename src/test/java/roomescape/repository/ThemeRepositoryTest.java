package roomescape.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import roomescape.model.Theme;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DataJpaTest
@Sql(scripts = {"/initialize_table.sql", "/test_data.sql"})
class ThemeRepositoryTest {

    @Autowired
    private ThemeRepository themeRepository;

    @DisplayName("테마를 조회한다.")
    @Test
    void should_find_all_themes() {
        List<Theme> themes = themeRepository.findAll();

        assertThat(themes).hasSize(3);
    }

    @DisplayName("테마를 저장한다.")
    @Test
    void should_save_theme() {
        themeRepository.save(new Theme("무빈", "공포", "공포.jpg"));

        List<Theme> themes = themeRepository.findAll();

        assertThat(themes).hasSize(4);
    }

    @DisplayName("아이디로 테마를 조회한다.")
    @Test
    void should_find_theme_when_give_theme_id() {
        Theme theme = themeRepository.findById(1L).get();

        assertThat(theme.getId()).isEqualTo(1L);
    }

    @DisplayName("테마를 삭제한다.")
    @Test
    void should_delete_theme() {
        themeRepository.deleteById(3L);

        assertThat(themeRepository.count()).isEqualTo(2);
    }

    @DisplayName("특정 기간의 테마를 인기순으로 정렬하여 조회한다.")
    @Sql(scripts = {"/initialize_table.sql", "/theme_data.sql"})
    @Test
    void should_find_ranking_theme_by_date() {
        LocalDate before = LocalDate.now().minusDays(8);
        LocalDate after = LocalDate.now();
        List<Theme> themes = themeRepository.findFirst10ByDateBetweenOrderByTheme(before, after);

        assertSoftly(softly -> {
            softly.assertThat(themes).hasSize(10);
            softly.assertThat(themes).containsExactly(
                    new Theme(10L, "name10", "description10", "thumbnail10"),
                    new Theme(9L, "name9", "description9", "thumbnail9"),
                    new Theme(1L, "name1", "description1", "thumbnail1"),
                    new Theme(2L, "name2", "description2", "thumbnail2"),
                    new Theme(3L, "name3", "description3", "thumbnail3"),
                    new Theme(4L, "name4", "description4", "thumbnail4"),
                    new Theme(5L, "name5", "description5", "thumbnail5"),
                    new Theme(6L, "name6", "description6", "thumbnail6"),
                    new Theme(7L, "name7", "description7", "thumbnail7"),
                    new Theme(8L, "name8", "description8", "thumbnail8")
            );
        });
    }
}
