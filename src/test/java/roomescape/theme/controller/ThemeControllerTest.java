package roomescape.theme.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.theme.dto.PopularThemeResponse;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class ThemeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ThemeService themeService;

    @Test
    @DisplayName("테마 생성 API")
    void saveTheme() throws Exception {
        // given
        ThemeRequest request = new ThemeRequest("방탈출 테마", "방탈출 테마 설명", "https://example.com/thumbnail.jpg");
        ThemeResponse response = new ThemeResponse(1L, "방탈출 테마", "방탈출 테마 설명", "https://example.com/thumbnail.jpg");

        given(themeService.saveTheme(any(ThemeRequest.class))).willReturn(response);

        // when && then
        mockMvc.perform(post("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("방탈출 테마"))
                .andExpect(jsonPath("$.description").value("방탈출 테마 설명"))
                .andExpect(jsonPath("$.thumbnail").value("https://example.com/thumbnail.jpg"))
                .andDo(document("theme/save",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").description("테마 이름"),
                                fieldWithPath("description").description("테마 설명"),
                                fieldWithPath("thumbnail").description("테마 썸네일 URL")
                        ),
                        responseFields(
                                fieldWithPath("id").description("테마 ID"),
                                fieldWithPath("name").description("테마 이름"),
                                fieldWithPath("description").description("테마 설명"),
                                fieldWithPath("thumbnail").description("테마 썸네일 URL")
                        )
                ));
    }

    @Test
    @DisplayName("모든 테마 조회 API")
    void findAll() throws Exception {
        // given
        List<ThemeResponse> responses = List.of(
                new ThemeResponse(1L, "테마1", "테마1 설명", "https://example.com/thumbnail1.jpg"),
                new ThemeResponse(2L, "테마2", "테마2 설명", "https://example.com/thumbnail2.jpg")
        );

        given(themeService.findAll()).willReturn(responses);

        // when && then
        mockMvc.perform(get("/themes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("테마1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("테마2"))
                .andDo(document("theme/find-all",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("테마 ID"),
                                fieldWithPath("[].name").description("테마 이름"),
                                fieldWithPath("[].description").description("테마 설명"),
                                fieldWithPath("[].thumbnail").description("테마 썸네일 URL")
                        )
                ));
    }

    @Test
    @DisplayName("인기 테마 순위 조회 API")
    void findAllPopular() throws Exception {
        // given
        List<PopularThemeResponse> responses = List.of(
                new PopularThemeResponse("테마1", "테마1 설명", "https://example.com/thumbnail1.jpg"),
                new PopularThemeResponse("테마2", "테마2 설명", "https://example.com/thumbnail2.jpg")
        );

        given(themeService.findAllPopular()).willReturn(responses);

        // when && then
        mockMvc.perform(get("/themes/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("테마1"))
                .andExpect(jsonPath("$[1].name").value("테마2"))
                .andDo(document("theme/find-popular",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].name").description("테마 이름"),
                                fieldWithPath("[].description").description("테마 설명"),
                                fieldWithPath("[].thumbnail").description("테마 썸네일 URL")
                        )
                ));
    }

    @Test
    @DisplayName("테마 삭제 API")
    void deleteTheme() throws Exception {
        // given
        Long themeId = 1L;
        doNothing().when(themeService).delete(anyLong());

        // when && then
        mockMvc.perform(delete("/themes/{themeId}", themeId))
                .andExpect(status().isNoContent())
                .andDo(document("theme/delete",
                        preprocessRequest(prettyPrint()),
                        pathParameters(
                                parameterWithName("themeId").description("삭제할 테마 ID")
                        )
                ));
    }
}
