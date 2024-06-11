package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import roomescape.controller.request.ThemeRequest;
import roomescape.model.Theme;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql(scripts = {"/initialize_table.sql", "/test_data.sql"})
class ThemeServiceTest {

    private ThemeReadService themeReadService;
    private ThemeWriteService themeWriteService;

    @Autowired
    public ThemeServiceTest(ThemeReadService themeReadService,
                            ThemeWriteService themeWriteService) {
        this.themeReadService = themeReadService;
        this.themeWriteService = themeWriteService;
    }

    @DisplayName("테마를 조회한다.")
    @Test
    void should_find_all_themes() {
        assertThat(themeReadService.findAllThemes()).hasSize(3);
    }

    @DisplayName("테마를 저장한다.")
    @Test
    void should_add_theme() {
        ThemeRequest themeRequest = new ThemeRequest("에버", "공포", "공포.jpg");

        themeReadService.addTheme(themeRequest);

        assertThat(themeReadService.findAllThemes()).hasSize(4);
    }

    @DisplayName("테마를 삭제한다.")
    @Test
    void should_delete_theme() {
        themeWriteService.deleteTheme(3L);

        assertThat(themeReadService.findAllThemes()).hasSize(2);
    }

    @DisplayName("최근 일주일 간 가장 인기 있는 테마 10개를 조회한다.")
    @Sql({"/initialize_table.sql", "/theme_data.sql"})
    @Test
    void should_find_popular_theme_of_week() {
        List<Theme> popularThemes = themeReadService.findPopularThemes();

        assertThat(popularThemes).containsExactly(
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
    }
}
