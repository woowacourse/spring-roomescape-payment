package roomescape.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.common.exception.LoginException;
import roomescape.common.util.DateTime;
import roomescape.common.util.JwtTokenContainer;
import roomescape.fixture.TestFixture;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.dto.request.LoginMember;
import roomescape.member.dto.request.LoginRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class LoginServiceMockTest {

    @Mock
    private JwtTokenContainer jwtTokenContainer;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private DateTime dateTime;

    @InjectMocks
    private LoginService loginService;

    @Test
    @DisplayName("유저 정보가 올바르지 않으면 예외가 발생한다.")
    void loginAndReturnToken_exception() {
        // given
        LoginRequest request = new LoginRequest("a", "b");
        when(memberRepository.findByEmailAndPassword("a", "b"))
                .thenReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> loginService.loginAndReturnToken(request))
                .isInstanceOf(LoginException.class);
    }

    @Test
    @DisplayName("정상적인 유저이면 토큰을 반환한다.")
    void loginAndReturnToken_test() {
        // given
        LoginRequest request = new LoginRequest("member@naver.com", "1234");
        when(dateTime.now())
                .thenReturn(LocalDateTime.now());
        when(memberRepository.findByEmailAndPassword("member@naver.com", "1234"))
                .thenReturn(Optional.of(TestFixture.createMember("멤버1", "member@naver.com", "1234")));
        when(jwtTokenContainer.createJwtToken(any(Member.class), any(LocalDateTime.class)))
                .thenReturn("realtoken");
        // when
        String token = loginService.loginAndReturnToken(request);
        // then
        assertThat(token).isEqualTo("realtoken");
    }
}
