package roomescape.theme.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.common.util.time.DateTime;
import roomescape.theme.exception.ThemeException;
import roomescape.theme.presentation.dto.PopularThemeResponse;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ThemeServiceTest {

    @Autowired
    private ThemeService themeService;

    @DisplayName("존재하는 예약의 테마는 삭제할 수 없다.")
    @Test
    void can_not_remove_exists_reservation() {
        Assertions.assertThatThrownBy(() -> themeService.deleteThemeById(1L))
                .isInstanceOf(ThemeException.class);
    }

    @DisplayName("인기 테마를 가져올 수 있다.")
    @Test
    void can_get_popular_theme() {
        List<PopularThemeResponse> popularThemes = themeService.getPopularThemes();

        Assertions.assertThat(popularThemes).containsExactly(
                new PopularThemeResponse("테마1", "재밌음", "/image/default.jpg"),
                new PopularThemeResponse("테마3", "놀라움", "/image/default.jpg")
        );
    }

    @TestConfiguration
    static class ThemeConfig {

        @Bean
        public DateTime dateTime() {
            return () -> LocalDateTime.of(2025, 4, 29, 10, 0);
        }
    }
}
