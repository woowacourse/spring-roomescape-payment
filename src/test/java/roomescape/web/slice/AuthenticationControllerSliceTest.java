package roomescape.web.slice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.web.ApiDocumentUtils.getDocumentRequest;
import static roomescape.web.ApiDocumentUtils.getDocumentResponse;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import roomescape.application.MemberService;
import roomescape.application.dto.request.member.LoginRequest;
import roomescape.application.dto.request.member.MemberInfo;
import roomescape.application.dto.request.member.SignupRequest;
import roomescape.support.mock.MockLoginMemberArgumentResolver;
import roomescape.web.api.AuthenticationController;
import roomescape.web.config.AdminHandlerInterceptor;
import roomescape.web.config.LoginMemberArgumentResolver;

@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private AdminHandlerInterceptor interceptor;

    @MockBean
    private LoginMemberArgumentResolver resolver;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true)
            .configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @BeforeEach
    public void setMockMvc(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthenticationController(memberService))
                .setCustomArgumentResolvers(new MockLoginMemberArgumentResolver())
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @DisplayName("회원가입")
    @Test
    void signup() throws Exception {
        SignupRequest request = new SignupRequest("재즈", "jazz@woowa.com", "123");

        given(memberService.signup(any())).willReturn(1L);

        ResultActions result = mockMvc.perform(post("/signup")
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(request)));

        result.andExpect(status().isCreated())
                .andDo(document("/signup",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("등록된 리소스 URI")
                        ))
                );
    }

    @DisplayName("로그인")
    @Test
    void login() throws Exception {
        LoginRequest request = new LoginRequest("jazz@woowa.com", "123");

        given(memberService.login(any())).willReturn("token=jwtToken");

        ResultActions result = mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(request)));

        result.andExpect(status().isOk())
                .andDo(document("/login",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.SET_COOKIE).description("JWT 토큰")
                        ))
                );
    }

    @DisplayName("로그아웃")
    @Test
    void logout() throws Exception {
        ResultActions result = mockMvc.perform(post("/logout"));

        result.andExpect(status().isOk())
                .andDo(document("/logout",
                        getDocumentRequest(),
                        getDocumentResponse())
                );
    }

    @DisplayName("로그인 체크")
    @Test
    void login_check() throws Exception {
        MemberInfo memberInfo = new MemberInfo(1L, "재즈");

        given(resolver.resolveArgument(any(), any(), any(), any())).willReturn(memberInfo);

        ResultActions result = mockMvc.perform(get("/login/check")
                .header(HttpHeaders.COOKIE, "token=jwtToken"));

        result.andExpect(status().isOk())
                .andDo(document("/login/check",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.COOKIE).description("JWT 토큰")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 아이디"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름")
                        ))
                );
    }
}
