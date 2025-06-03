package roomescape.presentation.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import roomescape.presentation.response.WaitingResponse;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class WaitingControllerTest {

    private static final Map<String, String> RESERVATION_BODY = Map.of(
            "date", "3000-03-17",
            "timeId", "1",
            "themeId", "1"
    );

    @Test
    @DisplayName("예약 대기 추가 요청시, id를 포함한 예약 대기 내용과 CREATED를 응답한다")
    void createWaiting() {
        var token = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "admin@email.com", "password", "password"))
                .when().post("/login")
                .then().statusCode(200)
                .extract().response().getDetailedCookies().getValue("token");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token) // 쿠키로 인증 정보 전달
                .body(RESERVATION_BODY)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("date", Matchers.equalTo("3000-03-17"));

    }

    @Test
    @DisplayName("예약 대기 삭제 요청시, 주어진 아이디에 해당하는 예약 대기가 있다면 삭제하고 NO CONTENT를 응답한다.")
    void deleteWaitingById() {
        var token = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "admin@email.com", "password", "password"))
                .when().post("/login")
                .then().statusCode(200)
                .extract().response().getDetailedCookies().getValue("token");

        WaitingResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token) // 쿠키로 인증 정보 전달
                .body(RESERVATION_BODY)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("date", Matchers.equalTo("3000-03-17"))
                .extract()
                .body().as(WaitingResponse.class);

        RestAssured.given().log().all()
                .when().delete("/waitings/" + response.id())
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
