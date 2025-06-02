package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import roomescape.dto.request.LoginRequest;
import roomescape.dto.request.MemberRegisterRequest;
import roomescape.service.auth.AuthService;
import roomescape.service.member.MemberService;
import roomescape.test_util.ServiceTest;

import javax.naming.AuthenticationException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

class AuthServiceTest extends ServiceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("사용자의 이메일, 비밀번호를 확인한 후 사용자의 아이디를 반환한다.")
    void loginTest() throws AuthenticationException {
        // given
        memberService.register(new MemberRegisterRequest("test@test.com", "testPassword", "test"));
        final LoginRequest loginRequest = new LoginRequest("test@test.com", "testPassword");
        MockHttpSession session = new MockHttpSession();

        // when
        authService.login(loginRequest, session);

        // then
        assertThat(session.getAttribute("id")).isNotNull();
    }

    @Test
    @DisplayName("사용자의 이메일을 찾을 수 없는 경우 예외가 발생한다")
    void noEmailLoginTest() {
        // given
        memberService.register(new MemberRegisterRequest("test@test.com", "testPassword", "test"));
        final LoginRequest loginRequest = new LoginRequest("wrongEmail@test.com", "testPassword");

        // when, then
        assertThatThrownBy(() -> authService.login(loginRequest, new MockHttpSession()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("사용자의 패스워드가 일치하지 않는 경우 예외가 발생한다.")
    void wrongPasswordLoginTest() {
        // given
        memberService.register(new MemberRegisterRequest("test@test.com", "testPassword", "test"));
        final LoginRequest loginRequest = new LoginRequest("test@test.com", "wrongPassword");

        // when, then
        assertThatThrownBy(() -> authService.login(loginRequest, new MockHttpSession()))
                .isInstanceOf(AuthenticationException.class);
    }
}
