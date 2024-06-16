package roomescape.service;

import io.jsonwebtoken.Claims;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.BaseTest;
import roomescape.model.Member;
import roomescape.service.fixture.MemberBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest extends BaseTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("주어진 사용자로 토큰을 생성한다.")
    @Test
    void should_create_token_when_given_member() {
        Member member = MemberBuilder.builder().build();
        String token = jwtTokenProvider.createToken(member);

        assertThat(token).isNotBlank();
    }

    @DisplayName("주어진 토큰으로 payload를 반환한다.")
    @Test
    void should_get_payload_when_given_token() {
        Member member = MemberBuilder.builder().build();
        String token = jwtTokenProvider.createToken(member);

        Claims claims = jwtTokenProvider.getPayload(token);

        String role = claims.get("role", String.class);
        String memberId = claims.getSubject();
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(memberId).isEqualTo("1");
            softAssertions.assertThat(role).isEqualTo("MEMBER");
        });
    }
}
