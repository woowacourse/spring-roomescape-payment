package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.BasicAcceptanceTest;
import roomescape.TestFixtures;
import roomescape.dto.response.theme.ThemePriceResponse;
import roomescape.dto.response.theme.ThemeResponse;
import roomescape.exception.NotFoundException;

class ThemeServiceTest extends BasicAcceptanceTest {
    @Autowired
    private ThemeService themeService;

    @DisplayName("해당 id의 테마를 삭제한다.")
    @Test
    void deleteById() {
        themeService.deleteById(6L);
        List<ThemeResponse> themeResponses = themeService.findAll();

        assertThat(themeResponses).isEqualTo(TestFixtures.THEME_RESPONSES_4);
    }

    @DisplayName("존재하지 않는 id로 테마를 삭제하면 예외가 발생한다.")
    @Test
    void shouldThrowIllegalArgumentExceptionWhenDeleteWithNonExistId() {
        assertThatCode(() -> themeService.deleteById(100L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("존재하지 않는 테마입니다.");
    }

    @DisplayName("해당 id의 테마 가격을 가져온다.")
    @Test
    void findThemePriceById() {
        ThemePriceResponse themePriceResponse = themeService.findThemePriceById(1L);

        assertThat(themePriceResponse.price()).isEqualTo(new BigDecimal("1000"));
    }
}
