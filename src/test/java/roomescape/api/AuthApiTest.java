package roomescape.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.TestFixture.USER_EMAIL;
import static roomescape.TestFixture.USER_NAME;
import static roomescape.TestFixture.USER_PASSWORD;

import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import roomescape.dto.login.LoginCheckResponse;
import roomescape.dto.login.LoginRequest;
import roomescape.infrastructure.auth.JwtProvider;

class AuthApiTest extends ApiBaseTest {

    @Autowired
    JwtProvider jwtProvider;

    @LocalServerPort
    int port;

    @Test
    void 토근_로그인() {
        String token = RestAssured
                .given().log().all()
                .port(port)
                .body(new LoginRequest(USER_EMAIL, USER_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/login")
                .then().log().all()
                .extract().cookie("token");

        Long userId = Long.parseLong(jwtProvider.getSubject(token));

        assertAll(
                () -> assertThat(jwtProvider.isValidateToken(token)).isTrue(),
                () -> assertThat(userId).isEqualTo(2)
        );
    }

    @Test
    void 로그인_사용자_조회() {
        Cookie cookieByLogin = getCookieByLogin(port, USER_EMAIL, USER_PASSWORD);

        LoginCheckResponse response = RestAssured
                .given().log().all()
                .port(port)
                .cookie(cookieByLogin)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/login/check")
                .then().log().all()
                .statusCode(HttpStatus.OK.value()).extract().as(LoginCheckResponse.class);

        assertThat(response.name()).isEqualTo(USER_NAME);
    }
}
