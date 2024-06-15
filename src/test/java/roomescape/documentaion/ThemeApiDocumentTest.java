package roomescape.documentaion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import roomescape.reservation.application.ThemeService;
import roomescape.reservation.dto.request.ThemeSaveRequest;
import roomescape.reservation.presentation.ThemeController;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.TestFixture.HORROR_THEME;
import static roomescape.TestFixture.THEME_THUMBNAIL;
import static roomescape.TestFixture.WOOTECO_THEME;
import static roomescape.TestFixture.WOOTECO_THEME_DESCRIPTION;
import static roomescape.TestFixture.WOOTECO_THEME_NAME;

class ThemeApiDocumentTest extends DocumentTest {
    private final ThemeService themeService = Mockito.mock(ThemeService.class);

    @Test
    @DisplayName("테마 조회 API")
    void getThemes() throws Exception {
        BDDMockito.given(themeService.findAll())
                .willReturn(List.of(WOOTECO_THEME(1L)));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/themes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(
                                "theme-find",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                getThemeArrayResponseFields()
                        )
                );
    }

    @Test
    @DisplayName("인기 테마 조회 API")
    void getPopularThemes() throws Exception {
        BDDMockito.given(themeService.findAllPopular())
                .willReturn(List.of(WOOTECO_THEME(1L), HORROR_THEME(2L)));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/themes/popular")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(
                                "theme-find-popular",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                getThemeArrayResponseFields()
                        )
                );
    }

    @Test
    @DisplayName("테마 생성 API")
    void postTheme() throws Exception {
        ThemeSaveRequest request = new ThemeSaveRequest(WOOTECO_THEME_NAME, WOOTECO_THEME_DESCRIPTION, THEME_THUMBNAIL);

        BDDMockito.given(themeService.create(any()))
                .willReturn(WOOTECO_THEME(1L));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document(
                                "theme-create",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("테마 이름"),
                                        fieldWithPath("description").type(JsonFieldType.STRING).description("테마 설명"),
                                        fieldWithPath("thumbnail").type(JsonFieldType.STRING).description("테마 썸네일")
                                ),
                                responseFields(
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("테마 식별자"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("테마 이름"),
                                        fieldWithPath("description").type(JsonFieldType.STRING).description("테마 설명"),
                                        fieldWithPath("thumbnail").type(JsonFieldType.STRING).description("테마 썸네일")
                                )
                        )
                );
    }

    @Test
    @DisplayName("테마 삭제 API")
    void deleteTheme() throws Exception {
        BDDMockito.willDoNothing()
                .given(themeService)
                .deleteById(anyLong());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/themes/{id}", anyLong()))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document(
                                "theme-delete",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("id").description("삭제 대상 테마 식별자")
                                )
                        )
                );
    }

    private ResponseFieldsSnippet getThemeArrayResponseFields() {
        return responseFields(
                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("테마 식별자"),
                fieldWithPath("[].name").type(JsonFieldType.STRING).description("테마 이름"),
                fieldWithPath("[].description").type(JsonFieldType.STRING).description("테마 설명"),
                fieldWithPath("[].thumbnail").type(JsonFieldType.STRING).description("테마 썸네일")
        );
    }

    @Override
    protected Object initController() {
        return new ThemeController(themeService);
    }
}
