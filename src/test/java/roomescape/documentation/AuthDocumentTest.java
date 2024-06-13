package roomescape.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.application.AuthService;
import roomescape.application.MemberService;
import roomescape.application.dto.request.LoginRequest;
import roomescape.application.dto.response.MemberResponse;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.presentation.api.AuthController;
import roomescape.presentation.dto.Accessor;

@WebMvcTest(AuthController.class)
class AuthDocumentTest extends AbstractDocumentTest {

    @MockBean
    private AuthService authService;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("로그인을 한다.")
    void login() throws Exception {
        LoginRequest request = new LoginRequest("user@gmail.com", "password");
        Member member = new Member(1L, "user@gmail.com", "password", "유저", Role.USER);
        MemberResponse response = MemberResponse.from(member);
        String token = "eyascGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzE3NzM3MjE2LCJleHAiOjE3MTc3NDA4MTZ9.IrneG4SngrbiGN55S70TjB2KzEvyoFzfVjt4D1Aih1A";

        when(authService.validatePassword(any()))
                .thenReturn(response);
        when(authService.createToken(any()))
                .thenReturn(token);

        mockMvc.perform(
                post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk(),
                content().json(objectMapper.writeValueAsString(response)),
                cookie().value("token", token)
        ).andDo(
                document("auth/login",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("id").description("식별자"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("role").description("역할")
                        ),
                        responseCookies(
                                cookieWithName("token").description("JWT 토큰")
                        )
                )
        );
    }

    @Test
    @DisplayName("로그인 상태를 검사한다.")
    void checkLogin() throws Exception {
        Member member = new Member(1L, "user@gmail.com", "password", "유저", Role.USER);
        MemberResponse response = MemberResponse.from(member);

        when(authArgumentResolver.supportsParameter(any()))
                .thenReturn(true);
        when(authArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(new Accessor(1L, "user@gmail.com", "유저", Role.USER));
        when(memberService.getById(anyLong()))
                .thenReturn(response);

        mockMvc.perform(
                get("/login/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", "{MEMBER_TOKEN}"))
        ).andExpectAll(
                status().isOk(),
                content().json(objectMapper.writeValueAsString(response))
        ).andDo(
                document("auth/login-check",
                        responseFields(
                                fieldWithPath("id").description("식별자"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("role").description("역할")
                        )
                )
        );
    }

    @Test
    @DisplayName("로그아웃을 한다.")
    void logout() throws Exception {
        mockMvc.perform(
                post("/logout")
                        .cookie(new Cookie("token", "{MEMBER_TOKEN}"))
        ).andExpectAll(
                status().isOk(),
                cookie().maxAge("token", 0)
        ).andDo(
                document("auth/logout")
        );
    }
}
