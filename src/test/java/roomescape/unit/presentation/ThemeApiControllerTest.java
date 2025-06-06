package roomescape.unit.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import roomescape.auth.AuthToken;
import roomescape.auth.jwt.JJWTJwtUtil;
import roomescape.business.model.entity.Member;
import roomescape.business.model.vo.UserRole;
import roomescape.business.service.MemberService;
import roomescape.business.service.ThemeService;
import roomescape.presentation.api.ThemeApiController;
import roomescape.presentation.dto.request.ThemeCreateRequest;
import roomescape.presentation.dto.response.ThemeResponse;

@WebMvcTest(value = {ThemeApiController.class, JJWTJwtUtil.class})
@ExtendWith(RestDocumentationExtension.class)
class ThemeApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JJWTJwtUtil jwtUtil;

    @MockitoBean
    private ThemeService themeService;

    @MockitoBean
    private MemberService memberService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint())
                )
                .build();
    }

    @Test
    void 테마_생성에_성공한다() throws Exception {
        // given
        ThemeCreateRequest request = new ThemeCreateRequest("theme1", "description1", "thumbnail1");
        ThemeResponse response = new ThemeResponse("themeId1", "theme1", "description1", "thumbnail1");
        AuthToken token = jwtUtil.createToken(
                Member.restore("name", UserRole.ADMIN.name(), "admin", "email1@domain.com", "password1"));
        given(themeService.addAndGet(request)).willReturn(response);
        // when
        ResultActions result = mockMvc.perform(post("/themes")
                .cookie(new Cookie("authToken", token.value()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        // then
        result.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/themes/themeId1"))
                .andExpectAll(
                        jsonPath("$.id").value("themeId1"),
                        jsonPath("$.name").value("theme1"),
                        jsonPath("$.description").value("description1"),
                        jsonPath("$.thumbnail").value("thumbnail1"))
                .andDo(document("create-theme",
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("thumbnail").type(JsonFieldType.STRING).description("썸네일 URL")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.STRING).description("테마 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("thumbnail").type(JsonFieldType.STRING).description("썸네일 URL")
                        )
                ));
    }

    @Test
    void 테마_전체_조회에_성공한다() throws Exception {
        // given
        ThemeResponse response1 = new ThemeResponse("themeId1", "theme1", "description1", "thumbnail1");
        List<ThemeResponse> response = List.of(response1);
        AuthToken token = jwtUtil.createToken(
                Member.create("name", "email1@domain.com", "password1"));
        given(themeService.getAll()).willReturn(response);
        // when
        ResultActions result = mockMvc.perform(get("/themes")
                .cookie(new Cookie("authToken", token.value())));
        // then
        result.andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$[0].id").value("themeId1"),
                        jsonPath("$[0].name").value("theme1"))
                .andDo(document("get-all-theme",
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("테마 리스트"),
                                fieldWithPath("[].id").type(JsonFieldType.STRING).description("테마 ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("[].description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("[].thumbnail").type(JsonFieldType.STRING).description("썸네일 URL")
                        )
                ));
    }

    @Test
    void 인기_테마_조회에_성공한다() throws Exception {
        // given
        List<ThemeResponse> response = List.of(new ThemeResponse("themeId1", "theme1", "description1", "thumbnail1"));
        given(themeService.getPopular()).willReturn(response);
        // when
        ResultActions result = mockMvc.perform(get("/themes/popular"));
        // then
        result.andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$[0].id").value("themeId1"),
                        jsonPath("$[0].name").value("theme1"))
                .andDo(document("get-theme-rank",
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("테마 리스트"),
                                fieldWithPath("[].id").type(JsonFieldType.STRING).description("테마 ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("[].description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("[].thumbnail").type(JsonFieldType.STRING).description("썸네일 URL")
                        )
                ));
    }

    @Test
    void 테마_삭제에_성공한다() throws Exception {
        // given
        AuthToken token = jwtUtil.createToken(
                Member.restore("name", UserRole.ADMIN.name(), "admin", "email1@domain.com", "password1"));
        // when
        ResultActions result = mockMvc.perform(delete("/themes/1")
                .cookie(new Cookie("authToken", token.value())));
        // then
        result.andExpect(status().isNoContent())
                .andDo(document("delete-theme"));
    }
}
