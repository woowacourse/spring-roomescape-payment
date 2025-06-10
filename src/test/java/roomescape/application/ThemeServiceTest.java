package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(ThemeService.class)
class ThemeServiceTest extends ServiceTest {

    @Autowired
    private ThemeService service;

    @Test
    @DisplayName("테마를 추가한다.")
    void register() {
        var theme = service.register(
            "포포는 힘들다",
            "우테코 레벨2를 탈출하는 내용입니다.",
            "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
        );

        var themes = service.findAllThemes();
        assertThat(themes).contains(theme);
    }

    @Test
    @DisplayName("테마를 삭제한다.")
    void removeById() {
        // given
        var theme = service.register(
            "포포는 힘들다",
            "우테코 레벨2를 탈출하는 내용입니다.",
            "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
        );

        // when
        service.removeById(theme.id());

        // then
        var themes = service.findAllThemes();
        assertThat(themes).doesNotContain(theme);
    }
}
