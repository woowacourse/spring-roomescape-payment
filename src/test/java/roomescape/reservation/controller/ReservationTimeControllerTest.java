package roomescape.reservation.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.RestClientControllerTest;
import roomescape.auth.token.TokenProvider;
import roomescape.member.model.MemberRole;

class ReservationTimeControllerTest extends RestClientControllerTest {

    @Autowired
    private TokenProvider tokenProvider;

    @DisplayName("전체 예약 시간 정보를 조회한다.")
    @Test
    void getReservationTimesTest() {
        RestAssured.given(spec).log().all()
                .filter(document("findAll-reservation-time"))
                .cookie("token", createUserAccessToken())
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(8));
    }

    @DisplayName("예약 가능한 시간을 조회한다.")
    @Test
    void getAvailableReservationTimes() {
        RestAssured.given(spec).log().all()
                .filter(document("findAll-available-times"))
                .cookie("token", createUserAccessToken())
                .queryParam("date", LocalDate.now().toString())
                .queryParam("theme-id", 1)
                .when().get("/available-reservation-times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(8));
    }

    private String createUserAccessToken() {
        return tokenProvider.createToken(3L, MemberRole.USER);
    }
}
