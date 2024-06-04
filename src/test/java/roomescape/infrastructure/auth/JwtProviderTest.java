package roomescape.infrastructure.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.TestFixture.USER_EMAIL;
import static roomescape.TestFixture.USER_ID;
import static roomescape.TestFixture.USER_NAME;
import static roomescape.TestFixture.USER_ROLE;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import roomescape.BaseTest;
import roomescape.domain.member.Role;
import roomescape.dto.member.MemberPayload;
import roomescape.util.DateUtil;

@SpringBootTest(classes = JwtProvider.class)
class JwtProviderTest extends BaseTest {

    @Autowired
    JwtProvider jwtProvider;

    @Value("${security.jwt.token.secret-key}")
    String secretKey;

    @Test
    void 사용자의_정보가_포함된_토큰을_생성() {
        //given
        MemberPayload memberPayLoad = createMemberPayLoad();

        //when
        String token = jwtProvider.createToken(memberPayLoad);

        //then
        Claims claims = jwtProvider.getClaims(token);
        Long id = Long.parseLong(jwtProvider.getSubject(token));
        String name = claims.get("name", String.class);
        String email = claims.get("email", String.class);
        Role role = Role.valueOf(claims.get("role", String.class));

        assertAll(
                () -> assertThat(Long.parseLong(memberPayLoad.id())).isEqualTo(id),
                () -> assertThat(memberPayLoad.name()).isEqualTo(name),
                () -> assertThat(memberPayLoad.email()).isEqualTo(email),
                () -> assertThat(memberPayLoad.role()).isEqualTo(role)
        );
    }

    @Test
    void 유효한_토큰일_경우_true() {
        //given
        MemberPayload memberPayLoad = createMemberPayLoad();
        String validToken = jwtProvider.createToken(memberPayLoad);

        //when
        boolean result = jwtProvider.isValidateToken(validToken);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void 유효한_토큰이_아닐_경우_false() {
        //given
        MemberPayload memberPayLoad = createMemberPayLoad();
        String expiredToken = createExpiredToken(memberPayLoad);

        //when
        boolean result = jwtProvider.isValidateToken(expiredToken);

        //then
        assertThat(result).isFalse();
    }

    private MemberPayload createMemberPayLoad() {
        return new MemberPayload(
                USER_ID.toString(),
                USER_NAME,
                USER_EMAIL,
                Role.valueOf(USER_ROLE)
        );
    }

    public String createExpiredToken(MemberPayload memberPayload) {
        Date currentTime = DateUtil.getCurrentTime();
        Date expirationTime = new Date(currentTime.getTime() - 10000);

        return Jwts.builder()
                .setSubject(memberPayload.id())
                .setExpiration(expirationTime)
                .claim("name", memberPayload.name())
                .claim("email", memberPayload.email())
                .claim("role", memberPayload.role().name())
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }
}
