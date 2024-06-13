package roomescape.reservation.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import roomescape.common.config.IntegrationTest;
import roomescape.common.util.CookieUtils;
import roomescape.reservation.controller.dto.request.TimeSaveRequest;

class ReservationTimeControllerTest extends IntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("모든 시간 조회 성공 시 200 응답을 받는다.")
    @Test
    void findAll() {
        RestAssured.given(this.spec).log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .filter(document("/times/findAll"))
                .when()
                .get("/times")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("예약 가능한 시간 조회 성공 시 200응답을 받는다.")
    @Test
    void findAvailableTimes() {
        saveAdminMember();
        saveThemeAsHorror();
        saveReservationTimeAsTen();
        saveSuccessReservationAsDateNow();

        RestAssured.given(this.spec)
                .param("date", LocalDate.now().toString())
                .param("theme-id", 1)
                .log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .filter(document("/times/available"))
                .when()
                .get("/times/available")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("resources.$", hasSize(1));
    }

    @DisplayName("시간 정보를 저장 성공 시 201 응답과 Location 헤더에 리소스 저장 경로를 받는다.")
    @Test
    void save() throws JsonProcessingException {
        TimeSaveRequest timeSaveRequest = new TimeSaveRequest(LocalTime.now());

        RestAssured.given(this.spec).log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(timeSaveRequest))
                .accept(ContentType.JSON)
                .filter(document("/times/save"))
                .when()
                .post("/times")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", "/times/1");
    }

    @DisplayName("이미 등록된 예약 시간을 저장하려하는 경우 실패한다.")
    @Test
    void failSaveWhenAlreadyHasDuplicatedStartAt() throws JsonProcessingException {
        saveReservationTimeAsTen();
        TimeSaveRequest timeSaveRequest = new TimeSaveRequest(LocalTime.parse("10:00"));

        RestAssured.given(this.spec).log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(timeSaveRequest))
                .accept(ContentType.JSON)
                .filter(document("/times/save/fail/already-saved"))
                .when()
                .post("/times")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("시간 삭제 성공시 204 응답을 받는다.")
    @Test
    void delete() {
        saveReservationTimeAsTen();

        RestAssured.given(this.spec).log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .filter(document("/times/delete"))
                .when()
                .delete("/times/{id}", 1L)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("삭제하려는 예약 시간에 예약된 방탈출 예약이 있는 경우 삭제에 실패한다.")
    @Test
    void failDeleteWhenAlreadyReservedInReservationTime() {
        saveReservationTimeAsTen();
        saveThemeAsHorror();
        saveMemberAsKaki();
        saveSuccessReservationAsDateNow();

        RestAssured.given(this.spec).log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .filter(document("/times/delete/fail/already-reserved"))
                .when()
                .delete("/times/{id}", 1L)
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
