package roomescape.auth.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import roomescape.config.RestDocsConfig;
import roomescape.global.auth.controller.AuthController;
import roomescape.global.auth.dto.CheckLoginResponse;
import roomescape.global.auth.dto.LoginRequest;
import roomescape.global.auth.dto.LoginResponse;
import roomescape.global.auth.infrastructure.AuthorizationExtractor;
import roomescape.global.auth.infrastructure.CookieManager;
import roomescape.global.auth.infrastructure.JwtProvider;
import roomescape.global.auth.service.AuthService;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.member.service.MemberService;

@Import({RestDocsConfig.class, AuthorizationExtractor.class, CookieManager.class})
@WebMvcTest(AuthController.class)
@ExtendWith(RestDocumentationExtension.class)
public class AuthControllerDocsTest {

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @Autowired
    private RestDocumentationResultHandler restDocs;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(
            WebApplicationContext webApplicationContext,
            RestDocumentationContextProvider provider
    ) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(provider)
                        .uris()
                        .withScheme("http")
                        .withHost("123.123.123.123")
                        .withPort(8080))
                .alwaysDo(restDocs)
                .build();
    }

    @Test
    void login() throws Exception {
        LoginRequest loginRequest = new LoginRequest("ind07152@naver.com", "asd");
        when(authService.login(any()))
                .thenReturn(new LoginResponse("accessToken"));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        cookie().value("token", "accessToken")
                )
                .andDo(restDocs.document(
                        responseCookies(
                                cookieWithName("token").description("Access Token for Login")
                        )
                ));
    }

    @Test
    void checkLogin() throws Exception {
        CheckLoginResponse response = new CheckLoginResponse("cogi");
        when(memberService.getMember(any()))
                .thenReturn(new Member("cogi", "a", "a", MemberRole.USER));
        when(jwtProvider.getRole(anyString()))
                .thenReturn(MemberRole.USER);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/login/check")
                        .cookie(new Cookie("token", "accessToken")))
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(response))
                )
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("Login Member Name")
                        )
                ));
    }

    @Test
    void logout() throws Exception {
        when(jwtProvider.getRole(anyString()))
                .thenReturn(MemberRole.USER);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/logout")
                        .cookie(new Cookie("token", "accessToken")))
                .andExpectAll(
                        status().isNoContent()
                )
                .andDo(restDocs.document(
                        responseCookies(
                                cookieWithName("token").description("Logout")
                        )
                ));
    }
}
