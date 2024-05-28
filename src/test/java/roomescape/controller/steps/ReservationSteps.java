package roomescape.controller.steps;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import roomescape.web.controller.request.MemberReservationRequest;

import static roomescape.Fixture.COOKIE_NAME;

public class ReservationSteps {

    public static ValidatableResponse createReservation(MemberReservationRequest request, String token) {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(COOKIE_NAME, token)
                .body(request)
                .when().post("/reservations")
                .then().log().all();
    }

    public static ValidatableResponse getMyReservation(String token) {
        return RestAssured.given().log().all()
                .cookie(COOKIE_NAME, token)
                .when().get("/reservations/mine")
                .then().log().all();
    }

    public static ValidatableResponse getReservations() {
        return RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all();
    }

    public static ValidatableResponse deleteReservation(Long id) {
        return RestAssured.given().log().all()
                .when().delete("/reservations/" + id)
                .then().log().all();
    }
}
