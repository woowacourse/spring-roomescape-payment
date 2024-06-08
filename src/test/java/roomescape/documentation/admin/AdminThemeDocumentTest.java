package roomescape.documentation.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
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
import roomescape.domain.exception.DomainNotFoundException;
import roomescape.domain.reservation.detail.Theme;
import roomescape.exception.BadRequestException;
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

    @Test
    @DisplayName("테마를 추가할 때, 이미 존재하는 테마 이름이면 실패한다.")
    void addThemeWithDuplicatedName() throws Exception {
        ThemeRequest request = new ThemeRequest("테마", "테마 설명", "https://example.com");

        doThrow(new BadRequestException("이미 존재하는 테마 이름입니다."))
                .when(themeService).addTheme(any());

        mockMvc.perform(
                post("/admin/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", "{ADMIN_TOKEN}"))
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                document("admin/themes/add/already-exist",
                        responseFields(
                                fieldWithPath("message").description("이미 존재하는 테마 이름입니다.")
                        ))
        );
    }

    @Test
    @DisplayName("테마를 삭제한다.")
    void deleteTheme() throws Exception {
        mockMvc.perform(
                delete("/admin/themes/{id}", 1)
                        .cookie(new Cookie("token", "{ADMIN_TOKEN}"))
        ).andExpectAll(
                status().isNoContent()
        ).andDo(
                document("admin/themes/delete")
        );
    }

    @Test
    @DisplayName("테마를 삭제할 때, 존재하지 않는 테마 아이디면 실패한다.")
    void deleteThemeWithNonExistentId() throws Exception {
        doThrow(new DomainNotFoundException("해당 id의 테마가 존재하지 않습니다. (id: 1)"))
                .when(themeService).deleteThemeById(anyLong());

        mockMvc.perform(
                delete("/admin/themes/{id}", 1)
                        .cookie(new Cookie("token", "{ADMIN_TOKEN}"))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(
                document("admin/themes/delete/not-exist",
                        responseFields(
                                fieldWithPath("message").description("해당 id의 테마가 존재하지 않습니다. (id: 1)")
                        ))
        );
    }

    @Test
    @DisplayName("테마를 삭제할 때, 이미 사용 중이면 실패한다.")
    void deleteThemeWithInUse() throws Exception {
        doThrow(new BadRequestException("해당 테마를 사용하는 예약이 존재합니다."))
                .when(themeService).deleteThemeById(anyLong());

        mockMvc.perform(
                delete("/admin/themes/{id}", 1)
                        .cookie(new Cookie("token", "{ADMIN_TOKEN}"))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                document("admin/themes/delete/using-theme",
                        responseFields(
                                fieldWithPath("message").description("해당 테마를 사용하는 예약이 존재합니다.")
                        ))
        );
    }
}
