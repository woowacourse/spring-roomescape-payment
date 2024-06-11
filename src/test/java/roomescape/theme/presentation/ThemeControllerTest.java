package roomescape.theme.presentation;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.fixture.ThemeFixture;
import roomescape.theme.service.ThemeService;
import roomescape.util.ControllerTest;

@WebMvcTest(ThemeController.class)
class ThemeControllerTest extends ControllerTest {

    @MockBean
    private ThemeService themeService;

    @DisplayName("전체 테마 목록을 읽는 요청을 처리할 수 있다")
    @Test
    void should_handle_get_themes_request_when_requested() throws Exception {
        List<ThemeResponse> themeResponses = List.of(
                new ThemeResponse(ThemeFixture.THEME_1),
                new ThemeResponse(ThemeFixture.THEME_2)
        );

        when(themeService.findAllTheme()).thenReturn(themeResponses);

        mockMvc.perform(get("/themes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(themeResponses)));
    }

    @DisplayName("인기 테마 목록을 읽는 요청을 처리할 수 있다")
    @Test
    void should_handle_get_popular_themes_request_when_requested() throws Exception {
        List<ThemeResponse> themeResponses = List.of(
                new ThemeResponse(ThemeFixture.THEME_1),
                new ThemeResponse(ThemeFixture.THEME_2)
        );

        when(themeService.findPopularTheme()).thenReturn(themeResponses);

        mockMvc.perform(get("/themes/popular"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(themeResponses)));
    }
}
