package roomescape.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.test.context.jdbc.Sql;
import roomescape.fixture.ReservationFixture;

import static org.hamcrest.Matchers.is;

class AdminReservationAcceptanceTest extends AcceptanceTest {
    @DisplayName("관리자는 예약을 추가할 수 있다.")
    @Test
    void createReservation() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .body(ReservationFixture.createAdminReservationRequest(admin, reservationDetail))
                .when().post("/admin/reservations")
                .then().log().all()
                .assertThat().statusCode(201);
    }

    @DisplayName("일반 사용자는 관리자 권한의 예약을 추가할 수 없다.")
    @Test
    void cannotCreateReservation() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", guestToken)
                .body(ReservationFixture.createAdminReservationRequest(admin, reservationDetail))
                .when().post("/admin/reservations")
                .then().log().all()
                .assertThat().statusCode(403)
                .body("message", is("권한이 없습니다. 관리자에게 문의해주세요."));
    }

    @DisplayName("관리자는 조건별로 예약 내역을 조회할 수 있다.")
    @Test
    void findByMemberAndTheme() {
        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .queryParam("memberId", guest.getId())
                .queryParam("themeId", reservationDetail.getTheme().getId())
                .when().get("/admin/reservations/search")
                .then().log().all()
                .assertThat().statusCode(200);
    }

    @DisplayName("일반 사용자는 조건별로 예약 내역을 조회할 수 없다.")
    @Test
    void cannotFindByMemberAndTheme() {
        RestAssured.given().log().all()
                .cookie("token", guestToken)
                .queryParam("memberId", guest.getId())
                .queryParam("themeId", reservationDetail.getTheme().getId())
                .when().get("/admin/reservations/search")
                .then().log().all()
                .assertThat().statusCode(403)
                .body("message", is("권한이 없습니다. 관리자에게 문의해주세요."));
    }

    @DisplayName("어드민이 예약을 취소한다.")
    @Test
    void deleteReservationByAdmin() {
        //given
        long reservationId = (int) RestAssured.given().contentType(ContentType.JSON)
                .cookie("token", guestToken)
                .body(ReservationFixture.createReservationRequest(reservationDetail))
                .when().post("/reservations")
                .then().extract().body().jsonPath().get("id");

        //when & then
        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .when().delete("/admin/reservations/" + reservationId)
                .then().log().all()
                .assertThat().statusCode(204);
    }

    @DisplayName("관리자가 일정이 지난 예약을 삭제하려고 하면 예외가 발생한다.")
    @TestFactory
    @Sql({"/truncate.sql", "/theme.sql", "/time.sql", "/reservation-past-detail.sql", "/reservation.sql"})
    void cannotDeletePastReservation() {
        //given
        long reservationId = 1;

        //when & then
        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .when().delete("/admin/reservations/" + reservationId)
                .then().log().all()
                .assertThat().statusCode(400)
                .body("message", is("이미 지난 예약은 삭제할 수 없습니다."));

    }

    @DisplayName("사용자는 예약을 취소할 수 없다.")
    @Test
    void cannotDeleteReservationByGuest() {
        //given
        long reservationId = (int) RestAssured.given().contentType(ContentType.JSON)
                .cookie("token", guestToken)
                .body(ReservationFixture.createReservationRequest(reservationDetail))
                .when().post("/reservations")
                .then().extract().body().jsonPath().get("id");

        //when & then
        RestAssured.given().log().all()
                .cookie("token", guestToken)
                .when().delete("/admin/reservations/" + reservationId)
                .then().log().all()
                .assertThat().statusCode(403)
                .body("message", is("권한이 없습니다. 관리자에게 문의해주세요."));
    }
}
