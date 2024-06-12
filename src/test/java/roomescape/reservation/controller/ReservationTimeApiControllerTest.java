package roomescape.reservation.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static roomescape.util.Fixture.TODAY;
import static roomescape.util.RestDocsFilter.CREATE_RESERVATION_TIME;
import static roomescape.util.RestDocsFilter.DELETE_RESERVATION_TIME;
import static roomescape.util.RestDocsFilter.GET_AVAILABLE_RESERVATION_TIME;
import static roomescape.util.RestDocsFilter.GET_ENTIRE_RESERVATION_TIME;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import roomescape.config.IntegrationTest;
import roomescape.reservation.dto.TimeSaveRequest;
import roomescape.util.CookieUtils;

class ReservationTimeApiControllerTest extends IntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("모든 시간 조회 성공 시 200 응답을 받는다.")
    @Test
    void findAll() {
        saveReservationTimeAsTen();

        RestAssured.given(spec).log().all()
                .filter(GET_ENTIRE_RESERVATION_TIME.getFilter())
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .get("/times")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("responses", hasSize(1));
    }

    @DisplayName("예약 가능한 시간 조회 성공 시 200응답을 받는다.")
    @Test
    void findAvailableTimes() {
        saveAdminMemberAsDuck();
        saveThemeAsHorror();
        saveReservationTimeAsTen();
        saveSuccessReservationAsDateNow();

        RestAssured.given(spec)
                .filter(GET_AVAILABLE_RESERVATION_TIME.getFilter())
                .param("date", TODAY.toString())
                .param("theme-id", 1)
                .log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .get("/times/available")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("responses", hasSize(1));
    }

    @DisplayName("시간 정보를 저장 성공 시 201 응답과 Location 헤더에 리소스 저장 경로를 받는다.")
    @Test
    void save() throws JsonProcessingException {
        TimeSaveRequest timeSaveRequest = new TimeSaveRequest(LocalTime.now());

        RestAssured.given(spec).log().all()
                .filter(CREATE_RESERVATION_TIME.getFilter())
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(timeSaveRequest))
                .accept(ContentType.JSON)
                .when()
                .post("/times")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", "/times/1")
                .body(notNullValue());
    }

    @DisplayName("시간 삭제 성공시 204 응답을 받는다.")
    @Test
    void delete() {
        RestAssured.given(spec).log().all()
                .filter(DELETE_RESERVATION_TIME.getFilter())
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .delete("/times/{id}", 1L)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
