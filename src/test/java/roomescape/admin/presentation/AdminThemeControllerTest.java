package roomescape.admin.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.admin.AdminHandlerInterceptor;
import roomescape.login.LoginMemberArgumentResolver;
import roomescape.theme.dto.ThemeAddRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;

@WebMvcTest(AdminThemeController.class)
class AdminThemeControllerTest {

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

    @DisplayName("테마 삭제 요청을 처리할 수 있다")
    @Test
    void should_handle_delete_theme_when_requested() throws Exception {
        when(adminHandlerInterceptor.preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class),
                any(Object.class))).thenReturn(true);

        mockMvc.perform(delete("/admin/themes/{id}", 1))
                .andExpect(status().isNoContent());

    }

    @DisplayName("테마 추가 요청을 처리할 수 있다")
    @Test
    void should_handle_post_theme_request_when_requested() throws Exception {

        when(adminHandlerInterceptor.preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class),
                any(Object.class))).thenReturn(true);

        ThemeAddRequest themeAddRequest = new ThemeAddRequest("리비 테마", "리비 설명", "리비 경로");
        ThemeResponse mockResponse = new ThemeResponse(1L, "리비 테마", "리비 설명", "리비 경로");
        when(themeService.saveTheme(themeAddRequest)).thenReturn(mockResponse);

        mockMvc.perform(post("/admin/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(themeAddRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/themes/" + mockResponse.id()));
    }
}
