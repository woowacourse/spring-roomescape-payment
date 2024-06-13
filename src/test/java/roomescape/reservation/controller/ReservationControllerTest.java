package roomescape.reservation.controller;

import static org.hamcrest.Matchers.hasSize;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import roomescape.common.config.ControllerTest;
import roomescape.common.util.CookieUtils;

class ReservationControllerTest extends ControllerTest {

    @DisplayName("예약 목록 조회에 성공하면 200 응답을 받는다.")
    @Test
    void findAll() {
        memberJdbcUtil.saveMemberAsKaki();
        themeJdbcUtil.saveThemeAsHorror();
        reservationTimeJdbcUtil.saveReservationTimeAsTen();
        reservationJdbcUtil.saveReservationAsDateNow();

        RestAssured.given().log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .get("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("회원별 예약 목록 조회에 성공하면 200 응답을 받는다.")
    @Test
    void findReservationsAndWaitingsByMember() {
        memberJdbcUtil.saveMemberAsKaki();
        themeJdbcUtil.saveThemeAsHorror();
        reservationTimeJdbcUtil.saveReservationTimeAsTen();
        reservationJdbcUtil.saveReservationAsDateNow();
        waitingJdbcUtil.saveWaitAsDateNow();

        RestAssured.given().log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .get("/reservations/mine")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("resources.$", hasSize(2));
    }

    @DisplayName("테마 아이디, 회원 아이디, 기간 조건 조회에 성공하면 200 응답을 받는다.")
    @Test
    void findAllBySearchCond() {
        memberJdbcUtil.saveAdminMember();
        themeJdbcUtil.saveThemeAsHorror();
        reservationTimeJdbcUtil.saveReservationTimeAsTen();
        reservationJdbcUtil.saveReservationAsDateNow();

        String yesterday = LocalDate.now().minusDays(1).toString();
        String tomorrow = LocalDate.now().plusDays(1).toString();
        RestAssured.given()
                .queryParam("themeId", 1)
                .queryParam("memberId", 1)
                .queryParam("dateFrom", yesterday)
                .queryParam("dateTo", tomorrow)
                .log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .when()
                .get("/reservations/search")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("resources.$", hasSize(1));
    }

    @DisplayName("예약을 성공적으로 제거하면 204 응답을 받는다.")
    @Test
    void delete() {
        memberJdbcUtil.saveMemberAsKaki();
        themeJdbcUtil.saveThemeAsHorror();
        reservationTimeJdbcUtil.saveReservationTimeAsTen();
        reservationJdbcUtil.saveReservationAsDateNow();

        RestAssured.given().log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .delete("/reservations/{id}", 1L)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
