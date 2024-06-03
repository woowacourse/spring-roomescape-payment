package roomescape.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.auth.dto.LoggedInMember;
import roomescape.auth.dto.LoginRequest;
import roomescape.exception.BadArgumentRequestException;
import roomescape.member.domain.Email;
import roomescape.member.domain.Member;
import roomescape.member.domain.Password;
import roomescape.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    private static final String TEST_SECRET_KEY = "this-is-test-secret-key-this-is-test-secret-key";

    @Mock
    private MemberRepository memberRepository;
    private TokenProvider tokenProvider = new TokenProvider(TEST_SECRET_KEY);
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(tokenProvider, memberRepository);
    }

    @DisplayName("토큰 생성 시, 해당 멤버가 없을 경우 예외를 던진다.")
    @Test
    void createTokenTest_whenMemberNotExist() {
        LoginRequest request = new LoginRequest("not_exist@abc.com", "1234");
        given(memberRepository.findByEmailAndPassword(new Email("not_exist@abc.com"), new Password("1234")))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.createToken(request))
                .isInstanceOf(BadArgumentRequestException.class)
                .hasMessage("해당 멤버가 존재하지 않습니다.");
    }

    @DisplayName("해당 토큰의 유저를 찾을 수 있다.")
    @Test
    void findLoggedInMemberTest() {
        String token = makeToken("브리", "bri@abc.com", "1234");
        given(memberRepository.findById(1L)).willReturn(Optional.of(new Member(1L, "브리", "bri@abc.com")));
        LoggedInMember expected = new LoggedInMember(1L, "브리", "bri@abc.com", false);

        LoggedInMember actual = authService.findLoggedInMember(token);

        assertThat(actual).isEqualTo(expected);
    }

    private String makeToken(String name, String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        given(memberRepository.findByEmailAndPassword(new Email(email), new Password(password)))
                .willReturn(Optional.of(new Member(1L, name, email)));
        return authService.createToken(request);
    }
}
