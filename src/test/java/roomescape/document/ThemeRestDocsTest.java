package roomescape.document;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import roomescape.controller.ThemeController;
import roomescape.document.config.RestDocsSupport;
import roomescape.dto.ThemeRequest;
import roomescape.dto.ThemeResponse;
import roomescape.fixture.ThemeFixture;
import roomescape.service.ThemeService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ThemeController.class)
public class ThemeRestDocsTest extends RestDocsSupport {

    @MockBean
    private ThemeService themeService;

    @Test
    public void save() throws Exception {
        ThemeRequest request = new ThemeRequest("theme1", "theme description", "https://thumbnail.com");
        ThemeResponse response = ThemeResponse.from(ThemeFixture.DEFAULT_THEME);
        given(themeService.save(any()))
                .willReturn(response);

        mockMvc.perform(post("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("name").description("테마 이름"),
                                fieldWithPath("description").description("테마 설명"),
                                fieldWithPath("thumbnail").description("테마 썸네일 URL")
                        ),
                        responseFields(
                                fieldWithPath("id").description("테마 id"),
                                fieldWithPath("name").description("테마 이름"),
                                fieldWithPath("description").description("테마 설명"),
                                fieldWithPath("thumbnail").description("테마 썸네일 URL")
                        )
                ));
    }

    @Test
    public void findAndOrderByPopularity() throws Exception {
        List<ThemeResponse> response = List.of(
                ThemeResponse.from(ThemeFixture.DEFAULT_THEME),
                ThemeResponse.from(ThemeFixture.DEFAULT_THEME)
        );
        given(themeService.findAndOrderByPopularity(any(), any(), anyInt()))
                .willReturn(response);

        mockMvc.perform(get("/themes/ranking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("start", "2024-06-01")
                        .param("end", "2024-06-07")
                        .param("count", "10"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("start").description("검색 시작 날짜"),
                                parameterWithName("end").description("검색 종료 날짜"),
                                parameterWithName("count").description("검색할 테마의 개수")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("테마 id"),
                                fieldWithPath("[].name").description("테마 이름"),
                                fieldWithPath("[].description").description("테마 설명"),
                                fieldWithPath("[].thumbnail").description("테마 썸네일 URL")
                        )
                ));
    }

    @Test
    public void delete() throws Exception {
        doNothing().when(themeService)
                .delete(anyLong());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/themes/{id}", ThemeFixture.DEFAULT_THEME.getId()))
                .andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("id").description("삭제할 테마의 id")
                        )
                ));
    }
}
