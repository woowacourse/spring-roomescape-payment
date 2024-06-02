package roomescape.application.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class JwtProviderTest {

    @Autowired
    private JwtProvider jwtProvider;

    private final Member member = new Member(1L, "jazz@jazz.com", "123", "재즈", Role.NORMAL);

    @DisplayName("회원 정보로 JWT 토큰을 생성한다")
    @Test
    void generate_token() {
        String token = jwtProvider.encode(member);

        assertNotNull(token);
    }

    @DisplayName("토큰 검증에 실패하면 예외를 발생시킨다.")
    @Test
    void throw_signature_exception_when_invalid_token() {
        String token = jwtProvider.encode(member);
        token += "invalid";

        String testToken = token;
        assertThatThrownBy(() -> jwtProvider.verifyToken(testToken))
                .isInstanceOf(JwtException.class)
                .hasMessage("JWT 토큰 검증에 실패하였습니다.");
    }

    @DisplayName("토큰이 유효하지 않으면 예외를 발생시킨다.")
    @Test
    void throw_malformed_jwt_exception_when_invalid_token() {
        String testToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwibmFtZSI6IkpvYnMgRG9lIiwicm9sZSI6IlVTRVIifQ";

        assertThatThrownBy(() -> jwtProvider.verifyToken(testToken))
                .isInstanceOf(JwtException.class)
                .hasMessage("JWT 토큰 구성이 올바르지 않습니다.");
    }

    @DisplayName("토큰 검증에 성공하면 payload 가 일치한다.")
    @Test
    void success_verify_token() {
        String token = jwtProvider.encode(member);

        Claims claims = jwtProvider.verifyToken(token);
        Long id = Long.parseLong(claims.getSubject());
        String name = claims.get("name", String.class);
        Role role = Role.of(claims.get("role", String.class));

        assertAll(
                () -> assertThat(id).isEqualTo(member.getId()),
                () -> assertThat(name).isEqualTo(member.getName()),
                () -> assertThat(role).isEqualTo(member.getRole())
        );
    }
}
