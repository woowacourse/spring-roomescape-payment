package roomescape.web.slice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.web.ApiDocumentUtils.getDocumentRequest;
import static roomescape.web.ApiDocumentUtils.getDocumentResponse;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import roomescape.application.ThemeService;
import roomescape.application.dto.request.theme.ThemeRequest;
import roomescape.application.dto.response.theme.ThemeResponse;
import roomescape.web.api.AdminThemeController;
import roomescape.web.config.AdminHandlerInterceptor;
import roomescape.web.config.LoginMemberArgumentResolver;

@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
@WebMvcTest(AdminThemeController.class)
public class AdminThemeControllerSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ThemeService themeService;

    @MockBean
    private AdminHandlerInterceptor interceptor;

    @MockBean
    private LoginMemberArgumentResolver resolver;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true)
            .configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @DisplayName("테마 저장")
    @Test
    void saveTheme() throws Exception {
        ThemeRequest request = new ThemeRequest("테마이름", "설명", "썸네일.jpg");
        ThemeResponse response = new ThemeResponse(1L, "테마 이름", "설명", "썸네일.jpg");

        given(interceptor.preHandle(any(), any(), any())).willReturn(true);
        given(themeService.saveTheme(any())).willReturn(response);

        ResultActions result = mockMvc.perform(post("/admin/themes")
                .header(HttpHeaders.COOKIE, "token=adminToken")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isCreated())
                .andDo(document("/admin/saveTheme",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.COOKIE).description("JWT 토큰")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("thumbnail").type(JsonFieldType.STRING).description("테마 썸네일 URL")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("테마 아이디"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("thumbnail").type(JsonFieldType.STRING).description("테마 썸네일 URL")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("등록된 리소스 URI")
                        ))
                );
    }

    @DisplayName("테마 삭제")
    @Test
    void deleteTheme() throws Exception {
        Long themeId = 1L;

        given(interceptor.preHandle(any(), any(), any())).willReturn(true);

        ResultActions result = mockMvc.perform(delete("/admin/themes/{idTheme}", themeId)
                .header(HttpHeaders.COOKIE, "token=adminToken"));

        result.andExpect(status().isNoContent())
                .andDo(document("/admin/deleteTheme",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.COOKIE).description("JWT 토큰")
                        ),
                        pathParameters(
                                parameterWithName("idTheme").description("테마 아이디")
                        ))
                );
    }
}
