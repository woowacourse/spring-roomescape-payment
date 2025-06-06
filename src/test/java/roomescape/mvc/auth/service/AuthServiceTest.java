package roomescape.mvc.auth.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import roomescape.exception.LoginFailException;
import roomescape.mvc.auth.dto.AccessTokenContent;
import roomescape.mvc.auth.request.LoginRequest;
import roomescape.mvc.auth.response.AccessTokenResponse;
import roomescape.mvc.member.domain.Member;
import roomescape.mvc.member.domain.Role;
import roomescape.mvc.member.repository.MemberRepository;
import roomescape.mvc.member.service.MemberQueryService;
import roomescape.utility.JwtTokenProvider;

@DataJpaTest
public class AuthServiceTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private MemberRepository memberRepository;

    private AuthService authService;
    private MemberQueryService memberQueryService;
    private JwtTokenProvider jwtTokenProvider;

    private Member member;

    @BeforeEach
    void beforeEach() {
        jwtTokenProvider = new JwtTokenProvider(
                "test_secret_key_test_secret_key_test_secret_key_test_secret_key",
                60000);
        memberQueryService = new MemberQueryService(memberRepository);
        authService = new AuthService(jwtTokenProvider, memberQueryService);

        member = entityManager.persist(
                Member.createWithoutId(Role.GENERAL, "회원", "member@test.com", "password123!"));

        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("로그인할 수 있다.")
    public class login {

        @DisplayName("올바른 이메일과 비밀번호로 로그인이 가능하다.")
        @Test
        void canLogin() {
            // given
            LoginRequest loginRequest = new LoginRequest(member.getEmail(), member.getPassword());

            entityManager.flush();
            entityManager.clear();

            // when
            AccessTokenResponse response = authService.login(loginRequest);

            // then
            AccessTokenContent actualTokenContent = jwtTokenProvider.parseAccessToken(response.accessToken());
            AccessTokenContent expectedTokenContent =
                    new AccessTokenContent(member.getId(), member.getRole(), member.getName());
            assertThat(actualTokenContent).isEqualTo(expectedTokenContent);
        }

        @DisplayName("계정이 존재하지 않을 경우 로그인이 불가능하다.")
        @Test
        void cannotLoginWithInvalidAccount() {
            // given
            LoginRequest wrongLoginRequest = new LoginRequest("wrong@test.com", "password123!");

            // when & then
            assertThatThrownBy(() -> authService.login(wrongLoginRequest))
                    .isInstanceOf(LoginFailException.class)
                    .hasMessage("이메일에 해당하는 회원이 존재하지 않습니다.");
        }

        @DisplayName("비밀번호가 맞지 않을 경우 로그인이 불가능하다.")
        @Test
        void cannotLoginWithInvalidPassword() {
            // given
            LoginRequest wrongLoginRequest = new LoginRequest("member@test.com", "wrongPassword123!");

            // when & then
            assertThatThrownBy(() -> authService.login(wrongLoginRequest))
                    .isInstanceOf(LoginFailException.class)
                    .hasMessage("로그인 정보가 올바르지 않습니다.");
        }
    }
}
