package roomescape.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.docs.ApiDocumentUtils.getDocumentRequest;
import static roomescape.docs.ApiDocumentUtils.getDocumentResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import roomescape.auth.controller.LoginController;
import roomescape.auth.controller.TokenCookieManager;
import roomescape.auth.dto.LoggedInMember;
import roomescape.auth.dto.LoginRequest;
import roomescape.auth.service.AuthService;
import roomescape.config.handler.AuthenticationArgumentResolver;

@WebMvcTest(controllers = LoginController.class)
@ExtendWith(RestDocumentationExtension.class
)
class LoginApiTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthService authService;
    @MockBean
    private TokenCookieManager tokenCookieManager;
    @MockBean
    private AuthenticationArgumentResolver authenticationArgumentResolver;
    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @DisplayName("토큰을 받아온다.")
    @Test
    void loginTest() throws Exception {
        LoginRequest request = new LoginRequest("test@email", "password");
        ResponseCookie responseCookie = ResponseCookie.from("token", "testToken")
                .httpOnly(true)
                .path("/")
                .maxAge(10)
                .build();

        given(authService.createToken(request))
                .willReturn("testToken");
        given(tokenCookieManager.createResponseCookie("testToken"))
                .willReturn(responseCookie);

        ResultActions result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
                .andDo(document(
                        "auth/login",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드")
                        )
                ));
    }

    @DisplayName("현재 로그인한 멤버 정보를 가져온다")
    @Test
    void loginCheckTest() throws Exception {
        LoggedInMember loggedInMember = new LoggedInMember(1L, "testMember", "test@email.com", true);
        Cookie cookie = new Cookie("token", "testToken");

        given(authenticationArgumentResolver.supportsParameter(any()))
                .willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(loggedInMember);

        ResultActions result = mockMvc.perform(get("/login/check")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andDo(document(
                        "auth/check",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름")
                        )
                ));
    }

    @DisplayName("로그아웃")
    @Test
    void logoutTest() throws Exception {
        ResponseCookie responseCookie = ResponseCookie.from("token")
                .httpOnly(true)
                .maxAge(0)
                .path("/")
                .build();

        given(tokenCookieManager.createLogoutResponseCookie())
                .willReturn(responseCookie);

        ResultActions result = mockMvc.perform(post("/logout"));

        result.andExpect(status().isOk())
                .andDo(document(
                        "auth/logout",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }
}
