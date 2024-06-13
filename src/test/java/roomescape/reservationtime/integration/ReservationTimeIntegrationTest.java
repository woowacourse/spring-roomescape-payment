package roomescape.reservationtime.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import roomescape.auth.domain.Token;
import roomescape.auth.provider.CookieProvider;
import roomescape.model.IntegrationTest;
import roomescape.reservationtime.dto.ReservationTimeRequest;

import java.time.LocalTime;

import static org.hamcrest.Matchers.is;

class ReservationTimeIntegrationTest extends IntegrationTest {

    @Test
    @DisplayName("시간을 잘 등록하고 삭제하고 확인이 가능하다.")
    void reservationTimePageWorks() {
        Token token = tokenProvider.getAccessToken(1);
        ResponseCookie cookie = CookieProvider.setCookieFrom(token);
        ReservationTimeRequest reservationTimeRequest = new ReservationTimeRequest(LocalTime.of(10, 0));

        RestAssured.given().log().all()
                .cookie(cookie.toString())
                .contentType(ContentType.JSON)
                .body(reservationTimeRequest)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(201);

        RestAssured.given().log().all()
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(4));

        RestAssured.given().log().all()
                .cookie(cookie.toString())
                .when().delete("/admin/times/3")
                .then().log().all()
                .statusCode(204);
    }
}
