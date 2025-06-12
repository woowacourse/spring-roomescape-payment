package roomescape.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import roomescape.member.dto.LoginRequest;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ViewIntegrationTest {

    @LocalServerPort
    int port;

    String sessionId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        final LoginRequest loginRequest = new LoginRequest("admin@email.com", "1234");
        sessionId = RestAssured.given().contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/login")
                .then()
                .extract().cookie("JSESSIONID");
    }

    @DisplayName("관리자 페이지를 응답한다.")
    @Test
    void adminView() {
        RestAssured.given().log().all()
                .sessionId(sessionId)
                .when().get("/admin")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .sessionId(sessionId)
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .sessionId(sessionId)
                .when().get("/admin/theme")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .sessionId(sessionId)
                .when().get("/admin/time")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .sessionId(sessionId)
                .when().get("/admin/reservation-waiting")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("사용자 페이지를 응답한다.")
    @Test
    void userView() {
        RestAssured.given().log().all()
                .sessionId(sessionId)
                .when().get("/")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .sessionId(sessionId)
                .when().get("/reservation")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .sessionId(sessionId)
                .when().get("/reservation-mine")
                .then().log().all()
                .statusCode(200);
    }
}
