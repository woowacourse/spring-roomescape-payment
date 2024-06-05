package roomescape.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.TestFixture.USER_EMAIL;
import static roomescape.TestFixture.USER_PASSWORD;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.dto.login.LoginMember;
import roomescape.dto.login.LoginRequest;
import roomescape.dto.token.TokenDto;
import roomescape.exception.RoomEscapeException;
import roomescape.service.ServiceBaseTest;

class AuthServiceTest extends ServiceBaseTest {

    @Autowired
    AuthService authService;

    @Test
    void 등록된_계정으로_로그인() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest(USER_EMAIL, USER_PASSWORD);

        // when
        TokenDto token = authService.login(loginRequest);

        // then
        LoginMember loginMember = authService.extractLoginMemberByToken(token);
        assertThat(loginMember.email()).isEqualTo(loginRequest.email());
    }

    @Test
    void 등록되지_않은_이메일로_로그인할_경우_예외_발생() {
        // given
        LoginRequest loginRequest = new LoginRequest("test99@email.com", "123456");

        // when, then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 잘못된_비밀번호로_로그인할_경우_예외_발생() {
        // given
        LoginRequest loginRequest = new LoginRequest(USER_EMAIL, "000000");

        // when, then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 검증되지_않은_토큰으로_로그인_계정을_확인할_경우_예외_발생() {
        // given
        TokenDto tokenDto = new TokenDto(null);

        // when, then
        assertThatThrownBy(() -> authService.extractLoginMemberByToken(tokenDto))
                .isInstanceOf(RoomEscapeException.class);
    }
}
