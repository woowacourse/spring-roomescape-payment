package roomescape.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Disabled
    @Test
    @DisplayName("Jwt 토큰을 잘 생성하는지 확인")
    void createToken() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

        String token = tokenService.createToken(1L, dateTime, Duration.between(dateTime, dateTime.plusHours(1)));
        String expected = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NjcxOTY3OTksIm1lbWJlcl9pZCI6MX0.sIqOH_FW8ugFJ0TYD1Cjpq-sBEs_BafloUY1g13wAL4";

        Assertions.assertThat(token).isEqualTo(expected);
    }

    @Test
    @DisplayName("Jwt 토큰을 잘 파싱하는지 확인")
    void findMemberIdFromToken() {
        long memberId = tokenService.findMemberIdFromToken(
                "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NjcxOTY3OTksIm1lbWJlcl9pZCI6MX0.sIqOH_FW8ugFJ0TYD1Cjpq-sBEs_BafloUY1g13wAL4");

        Assertions.assertThat(memberId).isEqualTo(1);
    }
}
