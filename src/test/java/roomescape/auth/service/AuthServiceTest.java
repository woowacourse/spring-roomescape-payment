package roomescape.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.fixture.MemberFixture.getMemberChoco;
import static roomescape.fixture.MemberFixture.getMemberClover;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.auth.controller.dto.LoginRequest;
import roomescape.auth.controller.dto.TokenResponse;
import roomescape.auth.domain.AuthInfo;
import roomescape.auth.service.dto.SignUpCommand;
import roomescape.exception.AuthenticationException;
import roomescape.exception.BadRequestException;
import roomescape.exception.ErrorType;
import roomescape.member.domain.Member;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.util.ServiceTest;

@DisplayName("회원 로직 테스트")
class AuthServiceTest extends ServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    AuthService authService;

    @DisplayName("토큰 생성에 성공한다.")
    @Test
    void createToken() {
        //given
        String password = "1234";
        Member member = memberRepository.save(getMemberChoco());
        LoginRequest loginRequest = new LoginRequest(member.getEmail(), password);

        //when
        TokenResponse token = authService.createToken(loginRequest);

        //then
        assertThat(tokenProvider.isToken(token.accessToken())).isTrue();
        assertThat(tokenProvider.getPayload(token.accessToken()).getValue()).isEqualTo(member.getEmail());
    }

    @DisplayName("토큰으로 사용자 정보 조회에 성공한다.")
    @Test
    void fetchByToken() {
        //given
        Member member = memberRepository.save(getMemberChoco());
        String accessToken = tokenProvider.createAccessToken(member.getEmail());

        //when
        AuthInfo authInfo = authService.fetchByToken(accessToken);

        //then
        assertAll(
                () -> assertThat(authInfo.getName()).isEqualTo(member.getName()),
                () -> assertThat(authInfo.getEmail()).isEqualTo(member.getEmail()),
                () -> assertThat(authInfo.getRole()).isEqualTo(member.getRole())
        );
    }

    @DisplayName("유효하지 않는 토큰에 예외가 발생한다.")
    @Test
    void fetchByToken_InvalidToken() {
        //given
        String invalidToken = "12f3m$%g2gdgdgd";

        //when & then
        assertThatThrownBy(() -> authService.fetchByToken(invalidToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(ErrorType.SECURITY_EXCEPTION.getMessage());
    }

    @DisplayName("사용자 회원가입에 성공한다.")
    @Test
    void signUp() {
        //given
        String password = "1234";
        SignUpCommand signUpCommand =
                new SignUpCommand(getMemberClover().getName(), getMemberClover().getEmail(), password);

        //when
        authService.signUp(signUpCommand);

        //then
        Optional<Member> memberOptional = memberRepository.findMemberByEmailAddress(getMemberClover().getEmail());

        assertAll(
                () -> assertThat(memberOptional).isNotNull(),
                () -> assertThat(memberOptional.get().getName()).isEqualTo(getMemberClover().getName())
        );
    }

    @DisplayName("중복 이메일 사용자 생성 시, 예외가 발생한다.")
    @Test
    void createDuplicatedEmail() {
        //given
        String password = "1234";
        memberRepository.save(getMemberChoco());
        SignUpCommand signUpCommand =
                new SignUpCommand(getMemberChoco().getName(), getMemberChoco().getEmail(), password);

        //when & then
        assertThatThrownBy(() -> authService.signUp(signUpCommand))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorType.DUPLICATED_EMAIL_ERROR.getMessage());
    }

    @DisplayName("존재하지 않는 사용자 정보 조회에 실패한다.")
    @Test
    void unauthorizedMember() {
        //given
        String invalidEmail = "invalidEmail@invalid.com";
        String accessToken = tokenProvider.createAccessToken(invalidEmail);

        //when & then
        assertThatThrownBy(() -> authService.fetchByToken(accessToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(ErrorType.TOKEN_PAYLOAD_EXTRACTION_FAILURE.getMessage());
    }
}
