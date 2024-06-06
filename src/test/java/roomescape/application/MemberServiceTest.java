package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.fixture.MemberFixture.MEMBER_JAZZ;
import static roomescape.fixture.MemberFixture.MEMBER_SUN;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import roomescape.application.dto.request.member.LoginRequest;
import roomescape.application.dto.request.member.SignupRequest;
import roomescape.application.security.JwtProvider;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.exception.member.AuthenticationFailureException;
import roomescape.exception.member.DuplicatedEmailException;
import roomescape.support.DatabaseCleanupListener;

@TestExecutionListeners(value = {
        DatabaseCleanupListener.class,
        DependencyInjectionTestExecutionListener.class
})
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    JwtProvider jwtProvider;

    @DisplayName("중복된 이메일로 회원가입을 시도할 경우 에러를 발생시킨다.")
    @Test
    void throw_exception_when_signup_duplicated_email() {
        memberRepository.save(MEMBER_JAZZ.create());
        SignupRequest signupRequest = new SignupRequest("sun", MEMBER_JAZZ.getEmail(), "123");

        assertThatThrownBy(() -> memberService.signup(signupRequest))
                .isInstanceOf(DuplicatedEmailException.class);
    }

    @DisplayName("회원을 정상 생성한다.")
    @Test
    void success_signup() {
        SignupRequest signupRequest = new SignupRequest("sun", MEMBER_SUN.getEmail(), "123");

        assertThatNoException()
                .isThrownBy(() -> memberService.signup(signupRequest));
    }

    @DisplayName("로그인 시 이메일 혹은 비밀번호가 일치하지 않으면 에러를 발생시킨다.")
    @Test
    void throw_exception_when_login_miss_match_email_or_password() {
        memberRepository.save(MEMBER_JAZZ.create());
        LoginRequest loginRequest = new LoginRequest(MEMBER_JAZZ.getEmail(), "4334");

        assertThatThrownBy(() -> memberService.login(loginRequest))
                .isInstanceOf(AuthenticationFailureException.class);
    }

    @DisplayName("로그인이 정상적으로 완료되면 토큰을 발급한다.")
    @Test
    void success_login_with_publish_token() {
        Member member = memberRepository.save(MEMBER_JAZZ.create());
        LoginRequest loginRequest = new LoginRequest(MEMBER_JAZZ.getEmail(), MEMBER_JAZZ.getPassword());

        String token = memberService.login(loginRequest);

        assertThat(token).isEqualTo("token=" + jwtProvider.encode(member));
    }

    @DisplayName("존재하지 않는 회원 아이디를 삭제하면 에러를 발생시킨다.")
    @Test
    void throw_exception_when_delete_not_exists_member_id() {
        assertThatThrownBy(() -> memberService.withdrawal(1L))
                .isInstanceOf(AuthenticationFailureException.class);
    }

    @DisplayName("회원을 정상적으로 삭제한다.")
    @Test
    void success_delete_member() {
        Member member = memberRepository.save(MEMBER_JAZZ.create());

        assertThatNoException()
                .isThrownBy(() -> memberService.withdrawal(member.getId()));
    }
}
