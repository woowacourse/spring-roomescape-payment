package roomescape.web.slice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.web.ApiDocumentUtils.getDocumentRequest;
import static roomescape.web.ApiDocumentUtils.getDocumentResponse;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import roomescape.application.ThemeService;
import roomescape.application.dto.response.theme.ThemeResponse;
import roomescape.web.api.MemberThemeController;
import roomescape.web.config.AdminHandlerInterceptor;
import roomescape.web.config.LoginMemberArgumentResolver;

@AutoConfigureRestDocs(outputDir = "build/generated-snippets", uriScheme = "https", uriHost = "docs.api.com")
@WebMvcTest(MemberThemeController.class)
public class MemberThemeControllerSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ThemeService themeService;

    @MockBean
    private AdminHandlerInterceptor interceptor;

    @MockBean
    private LoginMemberArgumentResolver resolver;

    @DisplayName("모든 테마 조회")
    @Test
    void findAllThemes() throws Exception {
        List<ThemeResponse> response = List.of(
                new ThemeResponse(1L, "테마1 이름", "설명", "썸네일.jpg"),
                new ThemeResponse(2L, "테마2 이름", "설명", "썸네일.jpg")
        );

        given(themeService.findAllTheme()).willReturn(response);

        ResultActions result = mockMvc.perform(get("/themes"));

        result.andExpect(status().isOk())
                .andDo(document("/themes/findAll",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("테마 아이디"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("[].description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("[].thumbnail").type(JsonFieldType.STRING).description("테마 썸네일 URL")
                        )
                ));
    }

    @DisplayName("인기 있는 테마 조회")
    @Test
    void findAllPopularThemes() throws Exception {
        List<ThemeResponse> response = List.of(
                new ThemeResponse(1L, "테마1 이름", "설명", "썸네일.jpg"),
                new ThemeResponse(2L, "테마2 이름", "설명", "썸네일.jpg")
        );

        given(themeService.findAllPopularThemes(any())).willReturn(response);

        ResultActions result = mockMvc.perform(get("/themes/ranking")
                .contentType("application/json"));

        result.andExpect(status().isOk())
                .andDo(document("/themes/findPopularThemes",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("테마 아이디"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("[].description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("[].thumbnail").type(JsonFieldType.STRING).description("테마 썸네일 URL")
                        )
                ));
    }
}
