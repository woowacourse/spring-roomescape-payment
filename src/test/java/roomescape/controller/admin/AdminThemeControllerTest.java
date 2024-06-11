package roomescape.controller.admin;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.snippet.Attributes;
import roomescape.controller.config.RestDocsTestSupport;
import roomescape.service.dto.request.ThemeSaveRequest;
import roomescape.service.dto.response.ThemeResponse;
import roomescape.service.dto.response.ThemeResponses;
import roomescape.service.reservation.ThemeService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminThemeController.class)
class AdminThemeControllerTest extends RestDocsTestSupport {

    @MockBean
    private ThemeService themeService;

    @Test
    @DisplayName("테마 저장")
    void saveTheme() throws Exception {
        //given
        ThemeSaveRequest request = new ThemeSaveRequest("테마 이름", "테마 설명", "테마 섬네일 링크");
        ThemeResponse response = new ThemeResponse(1L, "테마 이름", "테마 설명", "테마 섬네일 링크");

        Mockito.when(themeService.saveTheme(any())).thenReturn(response);

        mockMvc.perform(post("/admin/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", ADMIN_TOKEN))
                        .requestAttr("loginMember", ADMIN)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.name").value(response.name()))
                .andExpect(jsonPath("$.description").value(response.description()))
                .andExpect(jsonPath("$.thumbnail").value(response.thumbnail()))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("name")
                                        .type(STRING)
                                        .description("테마 이름")
                                        .attributes(new Attributes.Attribute("constraints", "30자 이내")),
                                fieldWithPath("description")
                                        .type(STRING)
                                        .description("테마 설명")
                                        .attributes(new Attributes.Attribute("constraints", "255자 이내")),
                                fieldWithPath("thumbnail")
                                        .type(STRING)
                                        .description("테마 섬네일 링크")
                                        .attributes(new Attributes.Attribute("constraints", "255자 이내"))),
                        responseFields(
                                fieldWithPath("id")
                                        .type(NUMBER)
                                        .description("테마 아이디")
                                        .attributes(new Attributes.Attribute("constraints", "양수인 테마 아이디입니다.")),
                                fieldWithPath("name")
                                        .type(STRING)
                                        .description("테마 이름"),
                                fieldWithPath("description")
                                        .type(STRING)
                                        .description("테마 설명"),
                                fieldWithPath("thumbnail")
                                        .type(STRING)
                                        .description("테마 섬네일 링크")
                        )
                ));
    }

    @Test
    void getThemes() throws Exception {
        ThemeResponses response = new ThemeResponses(
                List.of(
                        new ThemeResponse(1L, "theme1", "theme_description1", "theme_thumbnail_link1"),
                        new ThemeResponse(2L, "theme2", "theme_description2", "theme_thumbnail_link2")
                )
        );

        Mockito.when(themeService.getThemes()).thenReturn(response);

        mockMvc.perform(get("/admin/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", ADMIN_TOKEN))
                        .requestAttr("loginMember", ADMIN)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                                responseFields(
                                        fieldWithPath("themeResponses")
                                                .type(ARRAY)
                                                .description("전체 테마 목록"),
                                        fieldWithPath("themeResponses[].id")
                                                .type(NUMBER)
                                                .description("테마 아이디")
                                                .attributes(constraints("positive")),
                                        fieldWithPath("themeResponses[].name")
                                                .type(STRING)
                                                .description("테마 이름")
                                                .attributes(constraints("not null")),
                                        fieldWithPath("themeResponses[].description")
                                                .type(STRING)
                                                .description("테마 설명")
                                                .attributes(constraints("255자 이내")),
                                        fieldWithPath("themeResponses[].thumbnail")
                                                .type(STRING)
                                                .description("테마 섬네일 링크")
                                                .attributes(constraints("255자 이내"))
                                )
                        )
                );
    }

    @Test
    void deleteTheme() throws Exception {
        //given
        mockMvc.perform(delete("/admin/themes/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", ADMIN_TOKEN))
                        .requestAttr("loginMember", ADMIN)
                )
                .andExpect(status().isNoContent())
                .andDo(restDocs.document());
    }
}