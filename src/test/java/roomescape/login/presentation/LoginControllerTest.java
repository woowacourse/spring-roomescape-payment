package roomescape.login.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.auth.fixture.TokenFixture.DUMMY_TOKEN;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.global.exception.AuthenticationException;
import roomescape.global.exception.ErrorResponse;
import roomescape.login.dto.Accessor;
import roomescape.login.dto.LoginCheckResponse;
import roomescape.login.dto.LoginRequest;
import roomescape.login.service.AuthService;
import roomescape.util.ControllerTest;

@WebMvcTest(LoginController.class)
class LoginControllerTest extends ControllerTest {

    @MockBean
    private AuthService authService;

    @DisplayName("올바른 정보의 로그인 요청을 처리할 수 있다")
    @Test
    void should_include_cookie_when_login_request_success() throws Exception {
        LoginRequest loginRequest = new LoginRequest("email@gmail.com", "password");

        when(authService.login(any(LoginRequest.class))).thenReturn(DUMMY_TOKEN);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"));
    }

    @DisplayName("존재하지 않는 이메일의 로그인 요청을 처리할 수 있다")
    @Test
    void should_handle_login_request_with_no_exist_email() throws Exception {
        LoginRequest loginRequest = new LoginRequest("invalidEmail@gmail.com", "password");

        String errorMessage = "이메일: " + loginRequest.email() + " 해당하는 멤버를 찾을 수 없습니다";

        when(authService.login(any(LoginRequest.class))).thenThrow(
                new AuthenticationException(errorMessage));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(objectMapper.writeValueAsString(new ErrorResponse(errorMessage))));
    }

    @DisplayName("잘못된 비밀번호의 로그인 요청을 처리할 수 있다")
    @Test
    void should_handle_login_request_with_wrong_password() throws Exception {
        LoginRequest loginRequest = new LoginRequest("email@gmail.com", "wrong-password");

        String errorMessage = "비밀번호가 틀렸습니다";

        when(authService.login(any(LoginRequest.class))).thenThrow(
                new AuthenticationException(errorMessage));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(objectMapper.writeValueAsString(new ErrorResponse(errorMessage))));
    }

    @DisplayName("로그인 확인 요청 시 유효한 토큰 값이면 멤버의 이름을 반환한다")
    @Test
    void should_return_name_when_login_check_status_is_valid() throws Exception {
        LoginCheckResponse response = new LoginCheckResponse("이름");
        when(authService.checkLogin(any(Accessor.class))).thenReturn(response);

        mockMvc.perform(get("/login/check")
                        .cookie(MEMBER_COOKIE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @DisplayName("로그 아웃 요청 시 쿠키를 만료시켜 로그 아웃한다")
    @Test
    void should_expire_cookie_when_logout_requested() throws Exception {
        mockMvc.perform(post("/logout")
                        .cookie(MEMBER_COOKIE))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("token", 0));
    }
}
