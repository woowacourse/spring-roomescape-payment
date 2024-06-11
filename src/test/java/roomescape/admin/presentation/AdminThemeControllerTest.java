package roomescape.admin.presentation;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.theme.dto.ThemeAddRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;
import roomescape.util.ControllerTest;

@WebMvcTest(AdminThemeController.class)
class AdminThemeControllerTest extends ControllerTest {

    @MockBean
    private ThemeService themeService;

    @DisplayName("테마 삭제 요청을 처리할 수 있다")
    @Test
    void should_handle_delete_theme_when_requested() throws Exception {
        mockMvc.perform(delete("/admin/themes/{id}", 1)
                        .cookie(ADMIN_COOKIE))
                .andExpect(status().isNoContent());
    }

    @DisplayName("테마 추가 요청을 처리할 수 있다")
    @Test
    void should_handle_post_theme_request_when_requested() throws Exception {

        ThemeAddRequest request = new ThemeAddRequest("테마 이름", "테마 설명", "썸네일 경로");
        ThemeResponse response = new ThemeResponse(1L, "테마 이름", "테마 설명", "썸네일 경로");

        when(themeService.saveTheme(request)).thenReturn(response);

        mockMvc.perform(post("/admin/themes")
                        .cookie(ADMIN_COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/themes/" + response.id()))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}
