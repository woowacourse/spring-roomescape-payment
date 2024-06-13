package roomescape.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.application.ThemeService;
import roomescape.application.dto.response.ThemeResponse;
import roomescape.domain.reservation.detail.Theme;
import roomescape.presentation.api.ThemeController;

@WebMvcTest(ThemeController.class)
class ThemeDocumentTest extends AbstractDocumentTest {

    @MockBean
    private ThemeService themeService;

    @Test
    @DisplayName("테마 목록을 조회한다.")
    void getAllThemes() throws Exception {
        List<ThemeResponse> responses = List.of(
                ThemeResponse.from(new Theme(1L, "테마1", "테마1 설명", "https://example.com/image1.jpg")),
                ThemeResponse.from(new Theme(2L, "테마2", "테마2 설명", "https://example.com/image2.jpg"))
        );

        when(themeService.getAllThemes())
                .thenReturn(responses);

        mockMvc.perform(
                get("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk(),
                content().json(objectMapper.writeValueAsString(responses))
        ).andDo(
                document("themes/list",
                        responseFields(
                                fieldWithPath("[].id").description("테마 식별자"),
                                fieldWithPath("[].name").description("테마 이름"),
                                fieldWithPath("[].description").description("테마 설명"),
                                fieldWithPath("[].thumbnail").description("테마 이미지 URL")
                        ))
        );
    }

    @Test
    @DisplayName("인기 테마 목록을 조회한다.")
    void getPopularThemes() throws Exception {
        List<ThemeResponse> responses = List.of(
                ThemeResponse.from(new Theme(1L, "테마1", "테마1 설명", "https://example.com/image1.jpg")),
                ThemeResponse.from(new Theme(2L, "테마2", "테마2 설명", "https://example.com/image2.jpg"))
        );

        when(themeService.getPopularThemes(any(), any(), anyInt()))
                .thenReturn(responses);

        mockMvc.perform(
                get("/themes/popular")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate", "2024-05-08")
                        .param("endDate", "2024-05-09")
                        .param("limit", "2")
        ).andExpectAll(
                status().isOk(),
                content().json(objectMapper.writeValueAsString(responses))
        ).andDo(
                document("themes/list/popular",
                        queryParameters(
                                parameterWithName("startDate").description("조회 시작 날짜"),
                                parameterWithName("endDate").description("조회 종료 날짜"),
                                parameterWithName("limit").description("조회 개수")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("테마 식별자"),
                                fieldWithPath("[].name").description("테마 이름"),
                                fieldWithPath("[].description").description("테마 설명"),
                                fieldWithPath("[].thumbnail").description("테마 이미지 URL")
                        ))
        );
    }
}
