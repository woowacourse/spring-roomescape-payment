package roomescape.system.auth.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.member.service.MemberService;
import roomescape.system.auth.dto.LoginRequest;
import roomescape.system.auth.jwt.JwtHandler;
import roomescape.system.auth.jwt.dto.TokenDto;
import roomescape.system.exception.RoomEscapeException;

@SpringBootTest
@Import({AuthService.class, JwtHandler.class, MemberService.class})
class AuthServiceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("로그인 성공시 JWT accessToken 을 반환한다.")
    void loginSuccess() {
        // given
        Member member = memberRepository.save(new Member("이름", "test@test.com", "12341234", Role.MEMBER));

        // when
        TokenDto response = authService.login(new LoginRequest(member.getEmail(), member.getPassword()));

        // then
        assertThat(response.accessToken()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 회원 email 또는 password로 로그인하면 예외가 발생한다.")
    void loginFailByNotExistMemberInfo() {
        // given
        String notExistEmail = "invalid@test.com";
        String notExistPassword = "invalid1234";

        // when & then
        Assertions.assertThatThrownBy(() -> authService.login(new LoginRequest(notExistEmail, notExistPassword)))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    @DisplayName("존재하지 않는 회원의 memberId로 로그인 여부를 체크하면 예외가 발생한다.")
    void checkLoginFailByNotExistMemberInfo() {
        // given
        Long notExistMemberId = (long) (memberRepository.findAll().size() + 1);

        // when & then
        Assertions.assertThatThrownBy(() -> authService.checkLogin(notExistMemberId))
                .isInstanceOf(RoomEscapeException.class);
    }
}
