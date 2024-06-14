package roomescape.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import roomescape.advice.exception.RoomEscapeException;
import roomescape.auth.dto.LoggedInMember;
import roomescape.auth.dto.LoginRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AuthServiceTest {
    @LocalServerPort
    private int port;
    @Autowired
    private AuthService authService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("토큰 생성 시, 해당 멤버가 없을 경우 예외를 던진다.")
    @Test
    void createTokenTest_whenMemberNotExist() {
        LoginRequest request = new LoginRequest("not_exist@abc.com", "1234");

        assertThatThrownBy(() -> authService.createToken(request))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("해당 멤버가 존재하지 않습니다.");
    }

    @DisplayName("해당 토큰의 유저를 찾을 수 있다.")
    @Test
    void findLoggedInMemberTest() {
        String token = makeToken("bri@abc.com", "1234");
        LoggedInMember expected = new LoggedInMember(3L, "브리", "bri@abc.com", false);
        LoggedInMember actual = authService.findLoggedInMember(token);

        assertThat(actual).isEqualTo(expected);
    }

    private String makeToken(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);

        return authService.createToken(request);
    }
}
