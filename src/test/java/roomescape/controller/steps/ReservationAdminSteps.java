package roomescape.controller.steps;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

import static roomescape.Fixture.COOKIE_NAME;

public class ReservationAdminSteps {

    public static ValidatableResponse searchReservation(String query, String adminToken) {
        return RestAssured.given().log().all()
                .cookie(COOKIE_NAME, adminToken)
                .when().get("/admin/reservations/search?" + query)
                .then().log().all();
    }
}
