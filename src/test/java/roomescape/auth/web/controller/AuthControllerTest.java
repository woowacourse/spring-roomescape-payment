package roomescape.auth.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.auth.service.AuthService;
import roomescape.auth.web.controller.request.LoginRequest;
import roomescape.auth.web.controller.response.MemberNameResponse;
import roomescape.auth.web.cookie.CookieProvider;
import roomescape.auth.web.exception.NotAuthorizationException;
import roomescape.common.AbstractRestDocsTests;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest extends AbstractRestDocsTests {

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CookieProvider cookieProvider;

    @Test
    void 로그인_요청을_할_수_있다() throws Exception {
        // given
        LoginRequest request = new LoginRequest("user@email.com", "1234");
        given(authService.login(any())).willReturn("stub_token");
        given(cookieProvider.createTokenCookie(any())).willReturn(new Cookie("token", "stub_token"));

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ATS001"))
                .andExpect(jsonPath("$.message").value("로그인에 성공했습니다."))
                .andExpect(cookie().value("token", "stub_token"))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("email").description("회원 이메일")
                                        .attributes(constraints("이메일 형식")),
                                fieldWithPath("password").description("회원 비밀번호")
                                        .attributes(constraints("공백일 수 없습니다."))
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        ),
                        responseCookies(
                                cookieWithName("token")
                                        .description("인증토큰")
                                        .attributes(constraints("로그인 성공 시 발급되는 쿠키")))
                ));
    }

    @Test
    void 잘못된_로그인을_할_경우_예외를_반환한다() throws Exception {
        // given
        LoginRequest request = new LoginRequest("user@email.com", "wrong_password");
        given(authService.login(any())).willThrow(new NotAuthorizationException("로그인 정보가 올바르지 않습니다."));

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("ATF002"))
                .andExpect(jsonPath("$.message").value("인증 실패"));
    }

    @Test
    void 로그아웃_요청을_할_수_있다() throws Exception {
        given(cookieProvider.createExpiredTokenCookie()).willReturn(new Cookie("token", ""));

        mockMvc.perform(post("/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ATS002"))
                .andExpect(jsonPath("$.message").value("로그아웃에 성공했습니다."))
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    @Test
    void 로그인_상태를_확인할_수_있다() throws Exception {
        given(authService.checkLogin(any())).willReturn(new MemberNameResponse("유저1"));

        mockMvc.perform(get("/login/check")
                        .cookie(new Cookie("token", "stub_token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ATS003"))
                .andExpect(jsonPath("$.message").value("로그인 상태를 확인했습니다."))
                .andExpect(jsonPath("$.data.name").value("유저1"))
                .andDo(restDocs.document(
                                responseFields(
                                        fieldWithPath("code").description("응답 코드"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("data.name").description("회원 이름")
                                ),
                                requestCookies(cookieWithName("token").description("인증 토큰"))
                        )
                );
    }

    @Test
    void 잘못된_로그인이라면_예외를_반환한다() throws Exception {
        given(authService.checkLogin(any())).willThrow(NotAuthorizationException.class);

        mockMvc.perform(get("/login/check"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("ATF002"))
                .andExpect(jsonPath("$.message").value("인증 실패"))
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }
}
