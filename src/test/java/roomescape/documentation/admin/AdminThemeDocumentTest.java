package roomescape.documentation.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.application.ThemeService;
import roomescape.application.dto.request.ThemeRequest;
import roomescape.application.dto.response.ThemeResponse;
import roomescape.documentation.AbstractDocumentTest;
import roomescape.domain.reservation.detail.Theme;
import roomescape.presentation.api.admin.AdminThemeController;

@WebMvcTest(AdminThemeController.class)
class AdminThemeDocumentTest extends AbstractDocumentTest {

    @MockBean
    private ThemeService themeService;

    @Test
    @DisplayName("테마를 추가한다.")
    void addTheme() throws Exception {
        ThemeResponse response = ThemeResponse.from(new Theme(1L, "테마", "테마 설명", "https://example.com"));
        ThemeRequest request = new ThemeRequest("테마", "테마 설명", "https://example.com");

        when(themeService.addTheme(any()))
                .thenReturn(response);

        mockMvc.perform(
                post("/admin/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", "{ADMIN_TOKEN}"))
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isCreated(),
                content().json(objectMapper.writeValueAsString(response))
        ).andDo(
                document("admin/themes/add",
                        responseFields(
                                fieldWithPath("id").description("테마 식별자"),
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("description").description("설명"),
                                fieldWithPath("thumbnail").description("이미지 URL")
                        ))
        );

    }
}
