package roomescape.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.controller.AuthAdminInterceptor;
import roomescape.controller.AuthArgumentResolver;
import roomescape.controller.util.CookieHandler;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.dto.auth.LoginRequestDto;
import roomescape.service.query.AuthQueryService;
import roomescape.util.JwtTokenProvider;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@ActiveProfiles("test")
class AuthControllerTest {

    @MockitoBean
    JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    AuthQueryService authQueryService;

    @MockitoBean
    CookieHandler cookieHandler;

    @MockitoBean
    AuthArgumentResolver authArgumentResolver;

    @MockitoBean
    AuthAdminInterceptor authAdminInterceptor;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class MemberLoginTest {

        @BeforeEach
        void setUp() {
            when(authQueryService.publishLoginToken(any(LoginRequestDto.class)))
                    .thenAnswer(invocation -> {
                        LoginRequestDto dto = invocation.getArgument(0);
                        if ("hello@woowa.com".equals(dto.email()) && "password".equals(dto.password())) {
                            return "mockToken";
                        }
                        throw new roomescape.exception.NotFoundException("이메일이나 비밀번호가 올바르지 않습니다.");
                    });
            when(cookieHandler.createCookie(anyString(), anyString()))
                    .thenAnswer(invocation -> new Cookie(invocation.getArgument(0), invocation.getArgument(1)));
            when(authArgumentResolver.supportsParameter(any())).thenReturn(true);
            when(authArgumentResolver.resolveArgument(any(), any(), any(), any()))
                    .thenAnswer(invocation -> {
                        HttpServletRequest req = ((org.springframework.web.context.request.ServletWebRequest) invocation.getArgument(2)).getRequest();
                        Cookie[] cookies = req.getCookies();
                        String token = null;
                        if (cookies != null) {
                            for (Cookie c : cookies) {
                                if ("token".equals(c.getName())) {
                                    token = c.getValue();
                                }
                            }
                        }
                        if ("mockToken".equals(token)) {
                            return new roomescape.dto.auth.LoginInfo(new Member(1L, "가이온", "hello@woowa.com", Role.USER, "password"));
                        }
                        throw new roomescape.exception.UnauthorizationException("유효하지 않은 토큰");
                    });
        }

        @DisplayName("등록된 회원이라면 로그인을 할 수 있다")
        @Test
        void loginMemberTest() throws Exception {
            LoginRequestDto loginRequestDto = new LoginRequestDto("hello@woowa.com", "password");
            mockMvc.perform(post("/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequestDto)))
                    .andExpect(status().isOk());
        }

        @DisplayName("등록되지 않은 회원은 로그인할 수 없다")
        @Test
        void loginNotJoinedMemberTest() throws Exception {
            LoginRequestDto loginRequestDto = new LoginRequestDto("hello1@woowa.com", "password");
            mockMvc.perform(post("/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequestDto)))
                    .andExpect(status().isNotFound());
        }

        @DisplayName("로그인한 상태를 확인할 수 있다")
        @Test
        void loginCheckMemberTest() throws Exception {
            mockMvc.perform(get("/login/check")
                    .cookie(new Cookie("token", "mockToken")))
                    .andExpect(status().isOk());
        }

        @DisplayName("토큰이 올바르지 않으면 로그인 상태를 유지할 수 없다")
        @Test
        void loginCheckInvalidTokenMemberTest() throws Exception {
            mockMvc.perform(get("/login/check")
                    .cookie(new Cookie("token", "invalidToken")))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class MemberLogoutTest {

        String loginToken;

        @BeforeEach
        void setUpRegistration() {
            loginToken = "mockToken";
            when(cookieHandler.createCookie(anyString(), any())).thenAnswer(invocation -> new Cookie(invocation.getArgument(0), invocation.getArgument(1)));
        }

        @DisplayName("로그인 상태라면 로그아웃을 할 수있다")
        @Test
        void logoutMemberTest() throws Exception {
            mockMvc.perform(post("/logout")
                    .cookie(new Cookie("token", "mockToken")))
                    .andExpect(status().isOk());
        }
    }
}
