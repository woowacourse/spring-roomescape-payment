package roomescape.application.service;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.common.exception.NotFoundException;
import roomescape.common.exception.UnauthorizedException;
import roomescape.dto.request.LoginRequestDto;
import roomescape.infrastructure.db.MemberJpaRepository;
import roomescape.model.Member;
import roomescape.model.Role;

public class AuthServiceTest extends ServiceTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    AuthService authService;

    @Test
    @DisplayName("존재하지 않는 이메일에 대하여 로그인 요청을 하는 경우 예외가 발생한다.")
    void test1() {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto("invalidEmail@gmail.com", "password");

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequestDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("틀린 비밀번호로 로그인 요청을 하는 경우 예외가 발생한다.")
    void test2() {
        // given
        Member member = saveMember();
        LoginRequestDto loginRequestDto = new LoginRequestDto(
                member.getEmail(),
                "invalidPassword"
        );

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequestDto))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("존재하는 이메일과 일치하는 비밀번호로 로그인 요청을 하는 경우 예외가 발생하지 않는다.")
    void test3() {
        // given
        Member member = saveMember();
        LoginRequestDto loginRequestDto = new LoginRequestDto(
                member.getEmail(),
                member.getPassword()
        );

        // when & then
        Assertions.assertDoesNotThrow(() -> authService.login(loginRequestDto));
    }

    private Member saveMember() {
        return memberJpaRepository.save(new Member(
                "멤버",
                "email@example.com",
                "password",
                Role.ADMIN
        ));
    }
}
