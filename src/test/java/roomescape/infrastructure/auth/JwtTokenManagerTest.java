package roomescape.infrastructure.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.BasicAcceptanceTest;
import roomescape.exception.AuthorizedException;

class JwtTokenManagerTest extends BasicAcceptanceTest {
    private static final String TEST_SECRET_KEY = "i-appreciate-your-kindness-her0807";

    @DisplayName("이메일로 토큰을 생성한다.")
    @Test
    void createTokenTest() {
        JwtTokenManager jwtTokenManager = createJwtTokenManager(10000);
        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        String payload = String.valueOf(1L);

        String token = jwtTokenManager.createToken(payload);

        String subject = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        assertThat(payload).isEqualTo(subject);
    }

    @DisplayName("만료된 토큰의 payload를 추출하면 예외가 발생한다.")
    @Test
    void getPayloadExceptionTest() {
        JwtTokenManager jwtTokenManager = createJwtTokenManager(-1);
        String payload = String.valueOf(1L);
        String expiredToken = jwtTokenManager.createToken(payload);

        assertThatCode(() -> jwtTokenManager.getPayload(expiredToken))
                .isInstanceOf(AuthorizedException.class)
                .hasMessage("인증 유효기간이 만료되었습니다.");
    }

    @DisplayName("쿠키에서 토큰을 추출한다")
    @Test
    void extractTokenTest() {
        JwtTokenManager jwtTokenManager = createJwtTokenManager(10000);
        Cookie cookie = new Cookie("token", "value");
        Cookie[] cookies = new Cookie[]{cookie};

        String accessToken = jwtTokenManager.extractToken(cookies);

        assertThat(accessToken).isEqualTo("value");
    }

    @DisplayName("쿠키가 비어있는 경우 예외가 발생한다.")
    @Test
    void extractEmptyTokenTest() {
        JwtTokenManager jwtTokenManager = createJwtTokenManager(10000);
        Cookie[] cookies = new Cookie[1];

        assertThatCode(() -> jwtTokenManager.extractToken(cookies))
                .isInstanceOf(AuthorizedException.class)
                .hasMessage("인증에 실패했습니다.");
    }

    @DisplayName("쿠키에 토큰이 담겨있지 않은 경우 예외가 발생한다.")
    @Test
    void extractNotFoundTokenTest() {
        JwtTokenManager jwtTokenManager = createJwtTokenManager(10000);
        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie("test", "value");

        assertThatCode(() -> jwtTokenManager.extractToken(cookies))
                .isInstanceOf(AuthorizedException.class)
                .hasMessage("토큰이 존재하지 않습니다.");
    }

    private JwtTokenManager createJwtTokenManager(int validityInMilliseconds) {
        JwtTokenProperties jwtTokenProperties = new JwtTokenProperties();
        jwtTokenProperties.setSecretKey(TEST_SECRET_KEY);
        jwtTokenProperties.setExpireMilliseconds(validityInMilliseconds);
        return new JwtTokenManager(jwtTokenProperties);
    }
}
