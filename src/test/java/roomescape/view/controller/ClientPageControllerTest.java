package roomescape.view.controller;

import static org.hamcrest.Matchers.containsString;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
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
class ClientPageControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("/ 으로 GET 요청을 보내면 index 페이지와 200 OK 를 받는다.")
    void getMainPage() {
        RestAssured.given().log().all()
                .port(port)
                .header(new Header("Cookie", getAccessTokenCookieByLogin("email@email.com", "password")))
                .when().get("/")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("/reservation 으로 GET 요청을 보내면 방탈출 예약 페이지와 200 OK 를 받는다.")
    void getReservationPage() {
        RestAssured.given().log().all()
                .port(port)
                .header(new Header("Cookie", getAccessTokenCookieByLogin("email@email.com", "password")))
                .when().get("/reservation")
                .then().log().all()
                .statusCode(200);
    }


    @Test
    @DisplayName("로그인 없이 /reservation 으로 GET 요청을 보내면 로그인 페이지로 리다이렉트 된다.")
    void getReservationPageWithoutLogin() {
        RestAssured.given().log().all()
                .port(port)
                .when().get("/reservation")
                .then().log().all()
                .statusCode(200)
                .body(containsString("<title>Login</title>"));
    }

    @Test
    @DisplayName("/reservation-mine 으로 GET 요청을 보내면 방탈출 예약 페이지와 200 OK 를 받는다.")
    void getMyReservationPage() {
        RestAssured.given().log().all()
                .port(port)
                .header(new Header("Cookie", getAccessTokenCookieByLogin("email@email.com", "password")))
                .when().get("/reservation-mine")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("로그인 없이 /reservation-mine 으로 GET 요청을 보내면 로그인 페이지로 리다이렉트 된다.")
    void getMyReservationPageWithoutLogin() {
        RestAssured.given().log().all()
                .port(port)
                .when().get("/reservation-mine")
                .then().log().all()
                .statusCode(200)
                .body(containsString("<title>Login</title>"));
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
