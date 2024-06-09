package roomescape.documentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import roomescape.controller.api.ThemeController;
import roomescape.controller.dto.request.ThemeRequest;
import roomescape.service.ThemeService;
import roomescape.service.dto.response.ThemeResponse;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ThemeApiDocumentationTest extends BaseDocumentationTest {

    private final ThemeService themeService = Mockito.mock(ThemeService.class);
    private final Clock clock = Mockito.mock(Clock.class);

    @Test
    @DisplayName("모든 테마를 조회한다")
    void getAllThemes() throws Exception {
        when(themeService.getAllThemes()).thenReturn(List.of(
                new ThemeResponse(1L, "테마명1", "테마1 설명", "https://image.com/1"),
                new ThemeResponse(2L, "테마명2", "테마2 설명", "https://image.com/2")
        ));

        mockMvc.perform(get("/themes")
                        .cookie(memberCookie)
                )
                .andExpect(status().isOk())
                .andDo(document("theme/findAll",
                        responseFields(
                                fieldWithPath("list.[].id").description("테마 id"),
                                fieldWithPath("list.[].name").description("테마명"),
                                fieldWithPath("list.[].description").description("테마 설명"),
                                fieldWithPath("list.[].thumbnail").description("테마 이미지 경로")
                        )
                ));
    }

    @Test
    @DisplayName("테마를 추가한다")
    void addTheme() throws Exception {
        ThemeResponse response = new ThemeResponse(1L, "테마명", "테마 설명", "https://image.com");
        when(themeService.addTheme(any())).thenReturn(response);

        ThemeRequest request = new ThemeRequest("테마명", "테마 설명", "https://image.com");
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/themes")
                        .cookie(adminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isCreated())
                .andDo(document("theme/create",
                        requestFields(
                                fieldWithPath("name").description("테마명"),
                                fieldWithPath("description").description("테마 설명"),
                                fieldWithPath("thumbnail").description("테마 이미지 경로")
                        ),
                        responseFields(
                                fieldWithPath("id").description("테마 id"),
                                fieldWithPath("name").description("테마명"),
                                fieldWithPath("description").description("테마 설명"),
                                fieldWithPath("thumbnail").description("테마 이미지 경로")
                        )
                ));
    }

    @Test
    @DisplayName("테마를 삭제한다")
    void deleteThemeById() throws Exception {
        mockMvc.perform(delete("/themes/{id}", 1)
                        .cookie(adminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andDo(document("theme/delete",
                        pathParameters(
                                parameterWithName("id").description("테마 id")
                        )
                ));
    }

    @Test
    @DisplayName("인기 테마를 조회한다")
    void getPopularThemes() throws Exception {
        when(themeService.getPopularThemes(any(), any(), anyInt())).thenReturn(List.of(
                new ThemeResponse(1L, "테마명1", "테마1 설명", "https://image.com/1"),
                new ThemeResponse(2L, "테마명2", "테마2 설명", "https://image.com/2")
        ));
        when(clock.instant()).thenReturn(Clock.fixed(Instant.parse("2024-06-10T00:00:00Z"), ZoneId.of("UTC")).instant());

        mockMvc.perform(get("/themes/popular")
                        .cookie(memberCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate", "2024-06-10")
                        .param("endDate", "2024-06-17")
                        .param("limit", "10")
                )
                .andExpect(status().isOk())
                .andDo(document("theme/popular",
                        queryParameters(
                                parameterWithName("startDate").description("조회 시작 날짜").optional(),
                                parameterWithName("endDate").description("조회 종료 날짜").optional(),
                                parameterWithName("limit").description("테마 조회 개수").optional()
                        ),
                        responseFields(
                                fieldWithPath("list.[].id").description("테마 id"),
                                fieldWithPath("list.[].name").description("테마명"),
                                fieldWithPath("list.[].description").description("테마 설명"),
                                fieldWithPath("list.[].thumbnail").description("테마 이미지 경로")
                        )
                ));
    }

    @Override
    Object controller() {
        return new ThemeController(themeService, clock);
    }
}
