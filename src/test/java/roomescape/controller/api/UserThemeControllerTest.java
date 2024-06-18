package roomescape.controller.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.controller.dto.FindThemeResponse;
import roomescape.global.argumentresolver.AuthenticationPrincipalArgumentResolver;
import roomescape.global.auth.CheckRoleInterceptor;
import roomescape.global.auth.CheckUserInterceptor;
import roomescape.service.ThemeService;

@AutoConfigureRestDocs
@WebMvcTest(UserThemeController.class)
class UserThemeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ThemeService themeService;

    @MockBean
    private AuthenticationPrincipalArgumentResolver argumentResolver;

    @MockBean
    private CheckRoleInterceptor checkRoleInterceptor;

    @MockBean
    private CheckUserInterceptor checkUserInterceptor;

    @DisplayName("유저 전체 테마 조회")
    @Test
    void findAll() throws Exception {
        given(themeService.findAll())
            .willReturn(List.of(
                new FindThemeResponse(1L, "루터회관 탈출하기", "테마 설명1", "https://test.com/test1.jpg"),
                new FindThemeResponse(2L, "우리집 탈출하기", "테마 설명2", "https://test.com/test2.jpg"),
                new FindThemeResponse(3L, "교도소 탈출하기", "테마 설명3", "https://test.com/test3.jpg")
            ));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/themes"))
            .andDo(print())
            .andDo(document("themes/findAll",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("[].id").description("테마 ID"),
                    fieldWithPath("[].name").description("테마 이름"),
                    fieldWithPath("[].description").description("테마 설명"),
                    fieldWithPath("[].thumbnail").description("썸네일 이미지 URL")
                )
            ))
            .andExpect(status().isOk());
    }

    @DisplayName("유저 인기 테마 조회")
    @Test
    void findPopular() throws Exception {
        given(themeService.findPopular())
            .willReturn(List.of(
                new FindThemeResponse(1L, "1위 테마", "테마 설명1", "https://test.com/test1.jpg"),
                new FindThemeResponse(2L, "2위 테마", "테마 설명2", "https://test.com/test2.jpg"),
                new FindThemeResponse(3L, "3위 테마", "테마 설명3", "https://test.com/test3.jpg")
            ));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/themes/trending"))
            .andDo(print())
            .andDo(document("themes/findPopular",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("[].id").description("테마 ID"),
                    fieldWithPath("[].name").description("테마 이름"),
                    fieldWithPath("[].description").description("테마 설명"),
                    fieldWithPath("[].thumbnail").description("썸네일 이미지 URL")
                )
            ))
            .andExpect(status().isOk());
    }
}
