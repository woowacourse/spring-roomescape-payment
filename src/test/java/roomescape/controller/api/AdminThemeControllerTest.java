package roomescape.controller.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.controller.dto.CreateThemeRequest;
import roomescape.controller.dto.CreateThemeResponse;
import roomescape.global.argumentresolver.AuthenticationPrincipalArgumentResolver;
import roomescape.global.auth.CheckRoleInterceptor;
import roomescape.global.auth.CheckUserInterceptor;
import roomescape.service.ThemeService;

@AutoConfigureRestDocs
@WebMvcTest(AdminThemeController.class)
class AdminThemeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ThemeService themeService;

    @MockBean
    private AuthenticationPrincipalArgumentResolver argumentResolver;

    @MockBean
    private CheckRoleInterceptor checkRoleInterceptor;

    @MockBean
    private CheckUserInterceptor checkUserInterceptor;

    @BeforeEach
    void setUp() {
        given(checkRoleInterceptor.preHandle(any(), any(), any()))
            .willReturn(true);
    }

    @DisplayName("어드민 테마 저장")
    @Test
    void save() throws Exception {
        given(themeService.save(any(), any(), any()))
            .willReturn(new CreateThemeResponse(1L, "방탈출 테마명", "방탈출 테마 설명", "https://test.com/test.jpg"));

        String request = objectMapper.writeValueAsString(
            new CreateThemeRequest("테마명", "테마 설명", "https://test.com/test.jpg"));

        mockMvc.perform(post("/admin/themes")
                .content(request)
                .contentType(APPLICATION_JSON))
            .andDo(print())
            .andDo(document("admin/themes/save",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("name").description("테마 이름"),
                    fieldWithPath("description").description("테마 설명"),
                    fieldWithPath("thumbnail").description("테마 썸네일 URL")
                ),
                responseFields(
                    fieldWithPath("id").description("추가된 테마 ID"),
                    fieldWithPath("name").description("추가된 테마 이름"),
                    fieldWithPath("description").description("추가된 테마 설명"),
                    fieldWithPath("thumbnail").description("추가된 테마 썸네일 URL")
                )
            ))
            .andExpect(status().isCreated());
    }

    @DisplayName("어드민 테마 삭제")
    @Test
    void delete() throws Exception {
        doNothing()
            .when(themeService)
            .delete(1L);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/themes/{id}", 1))
            .andDo(print())
            .andDo(document("admin/themes/delete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("id").description("삭제할 테마 ID")
                )
            ))
            .andExpect(status().isNoContent());
    }
}
