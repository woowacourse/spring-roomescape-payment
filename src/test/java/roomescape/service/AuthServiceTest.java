package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.member.Member;
import roomescape.dto.request.LoginRequest;
import roomescape.dto.request.MemberRegisterRequest;
import roomescape.dto.response.MemberRegisterResponse;
import roomescape.service.auth.AuthService;
import roomescape.service.member.MemberService;
import roomescape.test_util.ServiceTest;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class AuthServiceTest extends ServiceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("사용자의 이메일, 비밀번호를 확인한 후 사용자의 아이디를 반환한다.")
    void authenticateTest() {
        // given
        memberService.addMember(new MemberRegisterRequest("test@test.com", "testPassword", "test"));
        final LoginRequest loginRequest = new LoginRequest("test@test.com", "testPassword");

        // when
        final Long memberId = authService.authenticate(loginRequest);

        // then
        final Member saved = memberService.getMemberById(memberId);
        assertThat(saved.getEmail()).isEqualTo("test@test.com");
        assertThat(saved.getName()).isEqualTo("test");
    }

    @Test
    @DisplayName("사용자의 이메일을 찾을 수 없는 경우 예외가 발생한다")
    void noEmailAuthenticateTest() {
        // given
        memberService.addMember(new MemberRegisterRequest("test@test.com", "testPassword", "test"));
        final LoginRequest loginRequest = new LoginRequest("wrongEmail@test.com", "testPassword");

        // when, then
        assertThatThrownBy(() -> authService.authenticate(loginRequest))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("사용자의 패스워드가 일치하지 않는 경우 예외가 발생한다.")
    void wrongPasswordAuthenticateTest() {
        // given
        memberService.addMember(new MemberRegisterRequest("test@test.com", "testPassword", "test"));
        final LoginRequest loginRequest = new LoginRequest("test@test.com", "wrongPassword");

        // when, then
        assertThatThrownBy(() -> authService.authenticate(loginRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("세션 아이디를 업데이트 한다")
    void updateSessoinId() {
        // given
        final MemberRegisterResponse response = memberService.addMember(
                new MemberRegisterRequest("test@test.com", "testPassword", "test")
        );
        final Member member = memberService.getMemberById(response.id());
        final String sessionIdBefore = member.getSessionId();

        // when
        authService.updateSessionIdByMemberId(member.getId(), "hello");

        // then
        final Member memberAfterChanged = memberService.getMemberById(response.id());
        assertAll(
                () -> assertThat(sessionIdBefore).isNull(),
                () -> assertThat(memberAfterChanged.getSessionId()).isEqualTo("hello")
        );
    }
}
