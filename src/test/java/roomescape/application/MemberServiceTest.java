package roomescape.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.application.dto.request.member.LoginRequest;
import roomescape.application.dto.request.member.SignupRequest;
import roomescape.exception.member.AuthenticationFailureException;
import roomescape.fixture.CommonFixture;

class MemberServiceTest extends BaseServiceTest {

    @Autowired
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        SignupRequest request = new SignupRequest(CommonFixture.username, CommonFixture.userMangEmail, CommonFixture.password);
        memberService.signup(request);
    }

    @DisplayName("로그인 시, 토큰을 반환한다")
    @Test
    void when_loginSuccess_then_getToken() {
        // given
        LoginRequest request = new LoginRequest(CommonFixture.userMangEmail, CommonFixture.password);

        // when, then
        Assertions.assertThatCode(() -> memberService.login(request))
                .doesNotThrowAnyException();
    }

    @DisplayName("로그인 실패 시, 예외를 반환한다")
    @Test
    void when_loginFail_then_throwException() {
        // given
        LoginRequest request = new LoginRequest(CommonFixture.userMangEmail, "wrong_password");

        // when, then
        Assertions.assertThatThrownBy(() -> memberService.login(request))
                .isExactlyInstanceOf(AuthenticationFailureException.class);
    }

    @DisplayName("회원 탈퇴 시, 로그인할 수 없다")
    @Test
    void when_withdrawal_then_cannotLogin() {
        // given
        LoginRequest request = new LoginRequest(CommonFixture.userMangEmail, CommonFixture.password);

        // when
        memberService.findAllMember().stream()
                .findFirst()
                .ifPresent(member -> memberService.withdrawal(member.id()));

        // then
        Assertions.assertThatThrownBy(() -> memberService.login(request))
                .isExactlyInstanceOf(AuthenticationFailureException.class);
    }
}
