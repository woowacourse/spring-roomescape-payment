package roomescape.service.theme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import roomescape.dto.theme.ThemeResponse;
import roomescape.service.ServiceBaseTest;

class ThemeSearchServiceTest extends ServiceBaseTest {

    @Autowired
    ThemeSearchService themeSearchService;

    @Test
    void 단일_테마_조회() {
        // when
        ThemeResponse themeResponse = themeSearchService.findTheme(1L);

        // then
        assertAll(
                () -> assertThat(themeResponse.id()).isEqualTo(1L),
                () -> assertThat(themeResponse.name()).isEqualTo("테마1"),
                () -> assertThat(themeResponse.description()).isEqualTo("테마1 설명 설명 설명"),
                () -> assertThat(themeResponse.thumbnail()).isEqualTo("thumbnail1.jpg")
        );
    }

    @Test
    void 전체_테마_조회() {
        // when
        List<ThemeResponse> allThemeResponse = themeSearchService.findAllThemes();

        // then
        assertThat(allThemeResponse).hasSize(12);
    }

    @Test
    void 최근_일주일을_기준으로_하여_해당_기간_내에_방문하는_예약이_많은_테마_10개를_조회() {
        // given, when
        List<Long> popularThemeIds = themeSearchService.findPopularThemes()
                .stream()
                .map(ThemeResponse::id)
                .toList();

        // then
        assertAll(
                () -> assertThat(popularThemeIds).hasSize(10),
                () -> assertThat(popularThemeIds.get(0)).isEqualTo(1L),
                () -> assertThat(popularThemeIds.get(1)).isEqualTo(2L),
                () -> assertThat(popularThemeIds).doesNotContain(12L)
        );
    }

    @Sql("/reset-data.sql")
    @Test
    void 최근_일주일을_기준으로_하여_해당_기간_내에_방문하는_예약이_많은_테마_10개를_조회_예약이_없는_경우() {
        // given, when
        List<ThemeResponse> popularThemes = themeSearchService.findPopularThemes();

        // then
        assertThat(popularThemes).isEmpty();
    }
}
