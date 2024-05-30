package roomescape.theme.presentation;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.admin.AdminHandlerInterceptor;
import roomescape.login.LoginMemberArgumentResolver;
import roomescape.theme.service.ThemeService;

@WebMvcTest(ThemeController.class)
class ThemeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ThemeService themeService;

    @MockBean
    private LoginMemberArgumentResolver loginMemberArgumentResolver;

    @MockBean
    private AdminHandlerInterceptor adminHandlerInterceptor;

    @DisplayName("전체 테마 목록을 읽는 요청을 처리할 수 있다")
    @Test
    void should_handle_get_themes_request_when_requested() throws Exception {
        when(themeService.findAllTheme()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/themes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @DisplayName("인기 테마 목록을 읽는 요청을 처리할 수 있다")
    @Test
    void should_handle_get_popular_themes_request_when_requested() throws Exception {
        when(themeService.findPopularTheme()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/themes/popular"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }
}
