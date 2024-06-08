package roomescape.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.controller.config.RestDocsTestSupport;
import roomescape.service.dto.request.PopularThemeRequest;
import roomescape.service.dto.response.ThemeResponse;
import roomescape.service.dto.response.ThemeResponses;
import roomescape.service.reservation.ThemeService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ThemeController.class)
class ThemeControllerTest extends RestDocsTestSupport {

    @MockBean
    private ThemeService themeService;

    @Test
    void getThemes() throws Exception{
        ThemeResponses response = new ThemeResponses(
                List.of(
                        new ThemeResponse(1L, "theme1", "theme_description1", "theme_thumbnail_link1"),
                        new ThemeResponse(2L, "theme2", "theme_description2", "theme_thumbnail_link2")
                )
        );

        Mockito.when(themeService.getThemes()).thenReturn(response);

        mockMvc.perform(get("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", USER_TOKEN))
                        .requestAttr("loginMember", USER)
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
                                                .attributes(constraints( "positive")),
                                        fieldWithPath("themeResponses[].name")
                                                .type(STRING)
                                                .description("테마 이름")
                                                .attributes(constraints( "not null")),
                                        fieldWithPath("themeResponses[].description")
                                                .type(STRING)
                                                .description("테마 설명")
                                                .attributes(constraints( "255자 이내")),
                                        fieldWithPath("themeResponses[].thumbnail")
                                                .type(STRING)
                                                .description("테마 섬네일 링크")
                                                .attributes(constraints( "255자 이내"))
                                )
                        )
                );
    }

    @Test
    void getPopularThemes() throws Exception {

        PopularThemeRequest request = new PopularThemeRequest(
                LocalDate.now().minusWeeks(1),
                LocalDate.now(),
                3
        );

        ThemeResponses response = new ThemeResponses(
                List.of(
                        new ThemeResponse(1L, "인기테마1", "인기테마_설명1", "인기테마_섬네일링크1"),
                        new ThemeResponse(2L, "인기테마2", "인기테마_설명2", "인기테마_섬네일링크2"),
                        new ThemeResponse(3L, "인기테마3", "인기테마_설명3", "인기테마_섬네일링크3")
                )
        );

        Mockito.when(themeService.getPopularThemes(any())).thenReturn(response);

        mockMvc.perform(get("/themes/popular")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", USER_TOKEN))
                        .requestAttr("loginMember", USER)
                        .param("startDate", request.startDate().toString())
                        .param("endDate", request.endDate().toString())
                        .param("limit", request.limit().toString())
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("startDate")
                                        .description("인기 테마 산정 시작 날짜"),
                                parameterWithName("endDate")
                                        .description("인기 테마 산정 종료 날짜"),
                                parameterWithName("limit")
                                    .description("인기 테마 산정 개수")
                                    .attributes(constraints( "not null / positive"))
                        ),
                        responseFields(
                                        fieldWithPath("themeResponses")
                                                .type(ARRAY)
                                                .description("인기 테마 목록"),
                                        fieldWithPath("themeResponses[].id")
                                                .type(NUMBER)
                                                .description("인기 테마 아이디")
                                                .attributes(constraints( "positive")),
                                        fieldWithPath("themeResponses[].name")
                                                .type(STRING)
                                                .description("인기 테마 이름")
                                                .attributes(constraints( "not null")),
                                        fieldWithPath("themeResponses[].description")
                                                .type(STRING)
                                                .description("인기 테마 설명")
                                                .attributes(constraints( "255자 이내")),
                                        fieldWithPath("themeResponses[].thumbnail")
                                                .type(STRING)
                                                .description("인기 테마 섬네일 링크")
                                                .attributes(constraints( "255자 이내"))
                                )
                        )
                );
    }
}