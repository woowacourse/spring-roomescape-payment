package roomescape.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import roomescape.controller.dto.request.LoginRequest;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.global.exception.AuthorizationException;
import roomescape.repository.MemberRepository;

@SpringBootTest
@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class LoginServiceTest {
    @Autowired
    private LoginService loginService;

    @Autowired
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("로그인")
    class Login {
        @DisplayName("성공: 이메일과 비밀번호 일치")
        @Test
        void login() {
            memberRepository.save(new Member("러너덕", "deock@test.com", "123a!", Role.USER));
            assertThatCode(() -> loginService.login(new LoginRequest("deock@test.com", "123a!")))
                    .doesNotThrowAnyException();
        }

        @DisplayName("실패: 비밀번호 불일치")
        @Test
        void login_InvalidPassword() {
            memberRepository.save(new Member("러너덕", "deock@test.com", "123a!", Role.USER));
            assertThatThrownBy(() -> loginService.login(new LoginRequest("deock@test.com", "123b!")))
                    .isInstanceOf(AuthorizationException.class)
                    .hasMessage("아이디 혹은 패스워드가 일치하지 않습니다.");
        }

        @DisplayName("실패: 존재하지 않는 이메일")
        @Test
        void login_NoSuchMember() {
            memberRepository.save(new Member("러너덕", "deock@test.com", "123a!", Role.USER));
            assertThatThrownBy(() -> loginService.login(new LoginRequest("duck@test.com", "123a!")))
                    .isInstanceOf(AuthorizationException.class)
                    .hasMessage("아이디 혹은 패스워드가 일치하지 않습니다.");
        }
    }
}
