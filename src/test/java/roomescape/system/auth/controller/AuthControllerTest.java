package roomescape.system.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.domain.repository.MemberRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/truncate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AuthControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("로그인에 성공하면 JWT accessToken을 응답 받는다.")
    void getJwtAccessTokenWhenlogin() {
        // given
        String email = "test@email.com";
        String password = "12341234";
        memberRepository.save(new Member("이름", email, password, Role.MEMBER));

        Map<String, String> loginParams = Map.of(
                "email", email,
                "password", password
        );

        // when
        Map<String, String> cookies = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .body(loginParams)
                .when().post("/login")
                .then().log().all().extract().cookies();

        // then
        assertThat(cookies.get("accessToken")).isNotNull();
    }

    @Test
    @DisplayName("로그인 검증 시, 회원의 name을 응답 받는다.")
    void checkLogin() {
        // given
        String email = "test@test.com";
        String password = "12341234";
        String accessTokenCookie = getAccessTokenCookieByLogin(email, password);

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .header("cookie", accessTokenCookie)
                .when().get("/login/check")
                .then()
                .body("data.name", is("이름"));
    }

    @Test
    @DisplayName("로그인 없이 검증요청을 보내면 401 Unauthorized 를 응답한다.")
    void checkLoginFailByNotAuthorized() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .when().get("/login/check")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("로그아웃 요청 시, accessToken 쿠키가 삭제된다.")
    void checkLogout() {
        // given
        String accessToken = getAccessTokenCookieByLogin("email@email.com", "password");

        // when & then
        RestAssured.given().log().all()
                .port(port)
                .header("cookie", accessToken)
                .when().post("/logout")
                .then()
                .statusCode(200)
                .cookie("accessToken", "");
    }

    @Test
    @DisplayName("로그인 없이 로그아웃 요청을 보내면 403 Forbidden 을 응답한다.")
    void checkLogoutFailByNotAuthorized() {
        RestAssured.given().log().all()
                .port(port)
                .when().post("/logout")
                .then()
                .statusCode(403);
    }

    private String getAccessTokenCookieByLogin(final String email, final String password) {
        memberRepository.save(new Member("이름", email, password, Role.ADMIN));

        Map<String, String> loginParams = Map.of(
                "email", email,
                "password", password
        );

        String accessToken = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .port(port)
                .body(loginParams)
                .when().post("/login")
                .then().log().all().extract().cookie("accessToken");

        return "accessToken=" + accessToken;
    }
}
