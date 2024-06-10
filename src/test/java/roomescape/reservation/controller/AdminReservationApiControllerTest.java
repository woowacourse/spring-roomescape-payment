package roomescape.reservation.controller;

import static org.hamcrest.Matchers.hasSize;
import static roomescape.util.Fixture.TODAY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import roomescape.config.IntegrationTest;
import roomescape.reservation.dto.AdminReservationSaveRequest;
import roomescape.util.CookieUtils;

class AdminReservationApiControllerTest extends IntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("테마 아이디, 회원 아이디, 기간 조건 조회에 성공하면 200 응답을 받는다.")
    @Test
    void findAllBySearchCond() {
        saveAdminMemberAsDuck();
        saveThemeAsHorror();
        saveReservationTimeAsTen();
        saveSuccessReservationAsDateNow();

        String yesterday = LocalDate.now().minusDays(1).toString();
        String tomorrow = LocalDate.now().plusDays(1).toString();
        RestAssured.given()
                .queryParam("themeId", 1)
                .queryParam("memberId", 1)
                .queryParam("dateFrom", yesterday)
                .queryParam("dateTo", tomorrow)
                .log().all()
                .cookie(CookieUtils.TOKEN_KEY, getAdminToken())
                .when()
                .get("/admin/reservations/search")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("responses", hasSize(1));
    }

    @DisplayName("관리자가 예약 대기 정보 조회에 성공하면 200 응답을 받는다.")
    @Test
    void findWaitingReservations() {
        RestAssured.given().log().all()
                .cookie(CookieUtils.TOKEN_KEY, getAdminToken())
                .accept(ContentType.JSON)
                .when()
                .get("/admin/reservations/waiting")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("관리자가 예약을 성공적으로 추가하면 201 응답과 Location 헤더에 리소스 저장 경로를 받는다.")
    @Test
    void saveAdminReservation() throws JsonProcessingException {
        saveAdminMemberAsDuck();
        saveThemeAsHorror();
        saveReservationTimeAsTen();

        AdminReservationSaveRequest reservationSaveRequest = new AdminReservationSaveRequest( TODAY, 1L, 1L, 1L);

        RestAssured.given().log().all()
                .cookie(CookieUtils.TOKEN_KEY, getAdminToken())
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(reservationSaveRequest))
                .accept(ContentType.JSON)
                .when()
                .post("/admin/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", "/reservations/1");
    }
}
