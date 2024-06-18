package roomescape.view.controller;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import java.util.Map;
import org.hamcrest.Matchers;
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
class AdminPageControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("관리자 권한이 있는 유저가 /admin 으로 GET 요청을 보내면 어드민 페이지와 200 OK 를 받는다.")
    void getAdminPageHasRole() {
        // given
        String adminAccessTokenCookie = getAdminAccessTokenCookieByLogin("admin@admin.com", "12341234");

        // when & then
        RestAssured.given().log().all()
                .port(port)
                .header(new Header("Cookie", adminAccessTokenCookie))
                .when().get("/admin")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("관리자 권한이 없는 유저가 /admin 으로 GET 요청을 보내면 로그인 페이지로 리다이렉트 된다.")
    void getAdminPageHasNotRole() {
        // given
        String accessTokenCookie = getAccessTokenCookieByLogin("member@member.com", "12341234");

        // when & then
        RestAssured.given().log().all()
                .port(port)
                .header(new Header("Cookie", accessTokenCookie))
                .when().get("/admin")
                .then().log().all()
                .statusCode(200)
                .body(Matchers.containsString("<title>Login</title>"));
    }

    @Test
    @DisplayName("/admin/reservation 으로 GET 요청을 보내면 어드민 예약 관리 페이지와 200 OK 를 받는다.")
    void getAdminReservationPageHasRole() {
        // given
        String adminAccessTokenCookie = getAdminAccessTokenCookieByLogin("admin@admin.com", "12341234");

        // when & then
        RestAssured.given().log().all()
                .port(port)
                .header(new Header("Cookie", adminAccessTokenCookie))
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("관리자 권한이 없는 유저가 /admin/reservation 으로 GET 요청을 보내면 로그인 페이지로 리다이렉트 된다.")
    void getAdminReservationPageHasNotRole() {
        // given
        String accessTokenCookie = getAccessTokenCookieByLogin("member@member.com", "12341234");

        // when & then
        RestAssured.given().log().all()
                .port(port)
                .header(new Header("Cookie", accessTokenCookie))
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(200)
                .body(Matchers.containsString("<title>Login</title>"));
    }

    @Test
    @DisplayName("/admin/time 으로 GET 요청을 보내면 어드민 예약 시간 관리 페이지와 200 OK 를 받는다.")
    void getAdminTimePageHasRole() {
        // given
        String adminAccessTokenCookie = getAdminAccessTokenCookieByLogin("admin@admin.com", "12341234");

        // when & then
        RestAssured.given().log().all()
                .port(port)
                .header(new Header("Cookie", adminAccessTokenCookie))
                .when().get("/admin/time")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("관리자 권한이 없는 유저가 /admin/time 으로 GET 요청을 보내면 로그인 페이지로 리다이렉트 된다.")
    void getAdminTimePageHasNotRole() {
        // given
        String accessTokenCookie = getAccessTokenCookieByLogin("member@member.com", "12341234");

        // when & then
        RestAssured.given().log().all()
                .port(port)
                .header(new Header("Cookie", accessTokenCookie))
                .when().get("/admin/time")
                .then().log().all()
                .statusCode(200)
                .body(Matchers.containsString("<title>Login</title>"));
    }

    @Test
    @DisplayName("관리자 권한이 있는 유저가 /admin/theme 으로 GET 요청을 보내면 어드민 테마 관리 페이지와 200 OK 를 받는다.")
    void getAdminThemePageHasRole() {
        // given
        String adminAccessTokenCookie = getAdminAccessTokenCookieByLogin("admin@admin.com", "12341234");

        // when & then
        RestAssured.given().log().all()
                .port(port)
                .header(new Header("Cookie", adminAccessTokenCookie))
                .when().get("/admin/theme")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("관리자 권한이 없는 유저가 /admin/theme 으로 GET 요청을 보내면 로그인 페이지로 리다이렉트 된다.")
    void getAdminThemePageHasNotRole() {
        // given
        String accessTokenCookie = getAccessTokenCookieByLogin("member@member.com", "12341234");

        // when & then
        RestAssured.given().log().all()
                .port(port)
                .header(new Header("Cookie", accessTokenCookie))
                .when().get("/admin/theme")
                .then().log().all()
                .statusCode(200)
                .body(Matchers.containsString("<title>Login</title>"));
    }

    @Test
    @DisplayName("관리자 권한이 있는 유저가 /admin/waiting 으로 GET 요청을 보내면 어드민 대기 관리 페이지와 200 OK 를 받는다.")
    void getAdminWatingPage() {
        // given
        String adminAccessTokenCookie = getAdminAccessTokenCookieByLogin("admin@email.com", "12341234");

        // when & then
        RestAssured.given().log().all()
                .port(port)
                .header(new Header("Cookie", adminAccessTokenCookie))
                .when().get("/admin/waiting")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("관리자 권한이 없는 유저가 /admin/waiting 으로 GET 요청을 보내면 로그인 페이지로 리다이렉트 된다.")
    void getAdminWaitingPageHasNotRole() {
        // given
        String accessTokenCookie = getAccessTokenCookieByLogin("member@email.com", "member");

        // when & then
        RestAssured.given().log().all()
                .port(port)
                .header(new Header("Cookie", accessTokenCookie))
                .when().get("/admin/waiting")
                .then().log().all()
                .statusCode(200)
                .body(Matchers.containsString("<title>Login</title>"));
    }


    private String getAdminAccessTokenCookieByLogin(String email, String password) {
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

    private String getAccessTokenCookieByLogin(String email, String password) {
        memberRepository.save(new Member("name", email, password, Role.MEMBER));
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
