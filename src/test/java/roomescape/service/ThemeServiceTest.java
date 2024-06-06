package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.BasicAcceptanceTest;
import roomescape.dto.response.theme.ThemePriceResponse;
import roomescape.exception.RoomescapeException;

class ThemeServiceTest extends BasicAcceptanceTest {
    @Autowired
    private ThemeService themeService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DisplayName("해당 id의 테마를 삭제한다.")
    @Test
    void deleteById() {
        jdbcTemplate.update("INSERT INTO theme (name, description, thumbnail, price) VALUES ('name1', 'description1', 'thumbnail1', 1000)");
        themeService.deleteById(1L);

        assertThat(themeService.findAll()).hasSize(0);
    }

    @DisplayName("존재하지 않는 id로 테마를 삭제하면 예외가 발생한다.")
    @Test
    void shouldThrowIllegalArgumentExceptionWhenDeleteWithNonExistId() {
        assertThatCode(() -> themeService.deleteById(100L))
                .isInstanceOf(RoomescapeException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @DisplayName("해당 id의 테마 가격을 가져온다.")
    @Test
    void findThemePriceById() {
        jdbcTemplate.update("INSERT INTO theme (name, description, thumbnail, price) VALUES ('name1', 'description1', 'thumbnail1', 1000)");
        ThemePriceResponse themePriceResponse = themeService.findThemePriceById(1L);

        assertThat(themePriceResponse.price()).isEqualTo(1000);
    }
}
