package roomescape.theme.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.common.AbstractRestDocsTests;
import roomescape.global.exception.NotFoundException;
import roomescape.theme.controller.request.ThemeCreateRequest;
import roomescape.theme.controller.response.ThemeResponse;
import roomescape.theme.service.ThemeService;

@WebMvcTest(controllers = ThemeApiController.class)
class ThemeApiControllerTest extends AbstractRestDocsTests {

    @MockitoBean
    private ThemeService themeService;

    @Test
    void 테마_목록을_조회할_수_있다() throws Exception {
        ThemeResponse response = new ThemeResponse(1L, "공포", "공포테마", "url");
        given(themeService.getAll()).willReturn(List.of(response));

        mockMvc.perform(get("/themes"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data[].id").description("테마 ID"),
                                fieldWithPath("data[].name").description("테마 이름"),
                                fieldWithPath("data[].description").description("테마 설명"),
                                fieldWithPath("data[].thumbnail").description("테마 썸네일")
                        )
                ));
    }

    @Test
    void 테마를_생성할_수_있다() throws Exception {
        ThemeCreateRequest request = new ThemeCreateRequest("공포", "공포테마", "url");
        ThemeResponse response = new ThemeResponse(1L, "공포", "공포테마", "url");
        given(themeService.create(any())).willReturn(response);

        mockMvc.perform(post("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("name").description("테마 이름"),
                                fieldWithPath("description").description("테마 설명"),
                                fieldWithPath("thumbnail").description("테마 썸네일")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.id").description("테마 ID"),
                                fieldWithPath("data.name").description("테마 이름"),
                                fieldWithPath("data.description").description("테마 설명"),
                                fieldWithPath("data.thumbnail").description("테마 썸네일")
                        )
                ));
    }

    @Test
    void 테마_생성시_유효성_예외가_발생한다() throws Exception {
        ThemeCreateRequest request = new ThemeCreateRequest("", "", "");

        mockMvc.perform(post("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 테마를_삭제할_수_있다() throws Exception {
        mockMvc.perform(delete("/themes/{id}", 1L))
                .andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("id").description("테마 ID")
                        )
                ));
    }

    @Test
    void 존재하지_않는_테마_삭제시_예외가_발생한다() throws Exception {
        doThrow(new NotFoundException("테마를 찾을 수 없습니다."))
                .when(themeService).deleteById(any());

        mockMvc.perform(delete("/themes/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void 인기_테마_목록을_조회할_수_있다() throws Exception {
        ThemeResponse response = new ThemeResponse(1L, "공포", "공포테마", "url");
        given(themeService.getPopularThemes()).willReturn(List.of(response));

        mockMvc.perform(get("/themes/popular"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data[].id").description("테마 ID"),
                                fieldWithPath("data[].name").description("테마 이름"),
                                fieldWithPath("data[].description").description("테마 설명"),
                                fieldWithPath("data[].thumbnail").description("테마 썸네일")
                        )
                ));
    }
}
