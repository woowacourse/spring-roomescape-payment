package roomescape.unit.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import roomescape.auth.AuthToken;
import roomescape.auth.jwt.JwtUtil;
import roomescape.business.model.entity.Member;
import roomescape.business.service.AuthService;
import roomescape.exception.auth.AuthenticationException;
import roomescape.infrastructure.MemberRepository;
import roomescape.presentation.dto.request.LoginRequest;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService sut;

    @Test
    void 올바른_이메일과_비밀번호로_인증에_성공한다() {
        // given
        String email = "test@example.com";
        String password = "password123";
        String encodedPassword = new BCryptPasswordEncoder().encode(password);
        Member member = Member.restore("user-id", "USER", "Test User", email, encodedPassword);
        AuthToken expectedAuth = mock(AuthToken.class);
        LoginRequest request = new LoginRequest(email, password);
        when(memberRepository.findByEmail_Value(email)).thenReturn(Optional.of(member));
        when(jwtUtil.createToken(member)).thenReturn(expectedAuth);

        // when
        AuthToken result = sut.authenticate(request);

        // then
        assertThat(result).isEqualTo(expectedAuth);
        verify(memberRepository).findByEmail_Value(email);
        verify(jwtUtil).createToken(member);
    }

    @Test
    void 존재하지_않는_이메일로_인증_시_예외가_발생한다() {
        // given
        String email = "nonexistent@example.com";
        String password = "password123";
        LoginRequest request = new LoginRequest(email, password);
        when(memberRepository.findByEmail_Value(email)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.authenticate(request))
                .isInstanceOf(AuthenticationException.class);

        verify(memberRepository).findByEmail_Value(email);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void 잘못된_비밀번호로_인증_시_예외가_발생한다() {
        // given
        String email = "test@example.com";
        String wrongPassword = "wrongPassword";
        LoginRequest request = new LoginRequest(email, wrongPassword);
        String correctPassword = "correctPassword";
        String encodedPassword = new BCryptPasswordEncoder().encode(correctPassword);
        Member member = Member.restore("user-id", "USER", "Test User", email, encodedPassword);

        when(memberRepository.findByEmail_Value(email)).thenReturn(Optional.of(member));

        // when, then
        assertThatThrownBy(() -> sut.authenticate(request))
                .isInstanceOf(AuthenticationException.class);

        verify(memberRepository).findByEmail_Value(email);
        verifyNoInteractions(jwtUtil);
    }
}
