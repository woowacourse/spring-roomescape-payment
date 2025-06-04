package roomescape.theme.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import fixture.ThemeFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.global.error.exception.ConflictException;
import roomescape.theme.dto.request.ThemeCreateRequest;
import roomescape.theme.entity.Theme;
import roomescape.theme.service.ThemeService;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ThemeIntegrationTest {

    @Autowired
    private ThemeService themeService;

    @Test
    @DisplayName("테마를 생성한다.")
    void createTheme() {
        // given
        Theme theme = ThemeFixture.createDefault();
        var request = new ThemeCreateRequest(
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail()
        );

        // when
        var response = themeService.createTheme(request);

        // then
        assertAll(
                () -> assertThat(response.id()).isEqualTo(1L),
                () -> assertThat(response.name()).isEqualTo(theme.getName()),
                () -> assertThat(response.description()).isEqualTo(theme.getDescription()),
                () -> assertThat(response.thumbnail()).isEqualTo(theme.getThumbnail())
        );
    }

    @Test
    @DisplayName("중복되는 테마 이름이 있을 경우 생성할 수 없다.")
    void createThemeWithDuplicateName() {
        // given
        Theme theme = ThemeFixture.createDefault();
        var request1 = new ThemeCreateRequest(
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail()
        );
        themeService.createTheme(request1);

        var request2 = new ThemeCreateRequest(
                theme.getName(),
                theme.getDescription() + "diff",
                theme.getThumbnail() + "diff"
        );

        // when & then
        assertThatThrownBy(() -> themeService.createTheme(request2))
                .isInstanceOf(ConflictException.class)
                .hasMessage("이미 존재하는 테마 이름입니다.");
    }

    @Test
    @DisplayName("모든 테마를 조회한다.")
    void getAllThemes() {
        // given
        List<Theme> themes = ThemeFixture.createDefaultList(2);
        var request1 = new ThemeCreateRequest(
                themes.get(0).getName(),
                themes.get(0).getDescription(),
                themes.get(0).getThumbnail()
        );
        var request2 = new ThemeCreateRequest(
                themes.get(1).getName(),
                themes.get(1).getDescription(),
                themes.get(1).getThumbnail()
        );
        themeService.createTheme(request1);
        themeService.createTheme(request2);

        // when
        var responses = themeService.getAllThemes();

        // then
        assertAll(
                () -> assertThat(responses).hasSize(2),
                () -> assertThat(responses.get(0).name()).isEqualTo(themes.get(0).getName()),
                () -> assertThat(responses.get(1).name()).isEqualTo(themes.get(1).getName())
        );
    }

    @Test
    @DisplayName("인기 있는 테마를 조회한다.")
    void getPopularThemes() {
        // given
        Theme theme1 = ThemeFixture.createDefault();
        var request1 = new ThemeCreateRequest(
                theme1.getName(),
                theme1.getDescription(),
                theme1.getThumbnail()
        );
        Theme theme2 = ThemeFixture.createDefault();
        var request2 = new ThemeCreateRequest(
                theme2.getName(),
                theme2.getDescription(),
                theme2.getThumbnail()
        );
        themeService.createTheme(request1);
        themeService.createTheme(request2);

        // when
        var responses = themeService.getPopularThemes(2);

        // then
        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("테마를 삭제한다.")
    void deleteTheme() {
        // given
        Theme theme = ThemeFixture.createDefault();
        var request = new ThemeCreateRequest(
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail()
        );
        var createdTheme = themeService.createTheme(request);

        // when
        themeService.deleteTheme(createdTheme.id());

        // then
        assertThat(themeService.getAllThemes()).isEmpty();
    }
}
