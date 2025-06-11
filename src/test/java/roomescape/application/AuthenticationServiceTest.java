package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static roomescape.fixture.UserFixture.CREATE_USER_1;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.domain.auth.AuthenticationInfo;
import roomescape.domain.auth.AuthenticationTokenHandler;
import roomescape.domain.user.User;
import roomescape.domain.user.UserRepository;
import roomescape.exception.AuthenticationException;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    AuthenticationTokenHandler tokenHandler;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    AuthenticationService authenticationService;

    @Nested
    @DisplayName("토큰을 발급한다.")
    class IssueToken {

        @Test
        @DisplayName("이메일이 틀린 경우 예외를 던진다.")
        void issueToken_WhenEmailInvalid_ThenThrowException() {
            // given
            String email = "invalid@email.com";
            String password = "password";

            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> authenticationService.issueToken(email, password))
                            .isInstanceOf(AuthenticationException.class)
                            .hasMessage("이메일이 틀렸습니다."),
                    () -> verify(userRepository).findByEmail(email)
            );
        }

        @Test
        @DisplayName("비밀번호가 틀린 경우 예외를 던진다.")
        void issueToken_WhenPasswordInvalid_ThenThrowException() {
            // given
            User user = CREATE_USER_1();
            String email = user.getEmail();
            String password = "invalidPassword";

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> authenticationService.issueToken(email, password))
                            .isInstanceOf(AuthenticationException.class)
                            .hasMessage("비밀번호가 틀렸습니다."),
                    () -> verify(userRepository).findByEmail(email)
            );
        }

        @Test
        @DisplayName("토큰을 정상적으로 발급한다.")
        void issueToken() {
            // given
            User user = CREATE_USER_1();
            String email = user.getEmail();
            String password = user.getPassword();

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

            AuthenticationInfo authenticationInfo = new AuthenticationInfo(user.getId(), user.getRole());

            when(tokenHandler.createToken(authenticationInfo)).thenReturn("createdToken");

            // when
            assertAll(
                    () -> assertThat(authenticationService.issueToken(email, password)).isEqualTo("createdToken"),
                    () -> verify(userRepository).findByEmail(anyString()),
                    () -> verify(tokenHandler).createToken(authenticationInfo)
            );
        }
    }

    @Nested
    @DisplayName("토큰의 사용자를 조회한다.")
    class GetUserByToken {

        @Test
        @DisplayName("토큰이 유효하지 않으면 예외를 던진다.")
        void getUserByToken_WhenInvalidToken_thenThrowException() {
            // given
            String token = "invalidToken";

            when(tokenHandler.isValidToken(anyString())).thenReturn(false);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> authenticationService.getUserByToken(token))
                            .isInstanceOf(AuthenticationException.class)
                            .hasMessage("토큰이 만료되었거나 유효하지 않습니다."),
                    () -> verify(tokenHandler).isValidToken(token)
            );
        }

        @Test
        @DisplayName("해당하는 ID의 사용자가 존재하지 않으면 예외를 던진다.")
        void getUserByToken_WhenUserNotExist_ThenThrowException() {
            // given
            String token = "validToken";

            when(tokenHandler.isValidToken(token)).thenReturn(true);
            when(tokenHandler.extractId(token)).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> authenticationService.getUserByToken(token))
                            .isInstanceOf(AuthenticationException.class)
                            .hasMessage("사용자 정보가 없습니다. 다시 로그인 해주세요."),
                    () -> verify(tokenHandler).extractId(anyString())
            );
        }

        @Test
        @DisplayName("토큰의 사용자를 정상적으로 조회한다.")
        void getUserByToken() {
            // given
            String token = "validToken";
            User user = CREATE_USER_1();

            when(tokenHandler.isValidToken(token)).thenReturn(true);
            when(tokenHandler.extractId(token)).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            // when
            User tokenUser = authenticationService.getUserByToken(token);

            // then
            assertAll(
                    () -> assertThat(tokenUser).isEqualTo(user),
                    () -> verify(tokenHandler).extractId(anyString()),
                    () -> verify(userRepository).findById(anyLong())
            );
        }
    }
}
