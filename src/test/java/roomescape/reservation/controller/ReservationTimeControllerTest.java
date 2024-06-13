package roomescape.reservation.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static roomescape.common.util.ApiDocumentUtils.getDocumentRequest;
import static roomescape.common.util.ApiDocumentUtils.getDocumentResponse;

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
import org.springframework.restdocs.payload.JsonFieldType;
import roomescape.common.config.ControllerTest;
import roomescape.common.util.CookieUtils;
import roomescape.reservation.controller.dto.request.TimeSaveRequest;
import roomescape.reservation.domain.ReservationTime;

class ReservationTimeControllerTest extends ControllerTest {

    private static final String ROOT_IDENTIFIER = "reservation-time";

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("모든 시간 조회 성공 시 200 응답을 받는다.")
    @Test
    void findAll() {
        RestAssured.given().log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .get("/times")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("예약 가능한 시간 조회 성공 시 200응답을 받는다.")
    @Test
    void findAvailableTimes() {
        memberJdbcUtil.saveAdminMember();
        themeJdbcUtil.saveThemeAsHorror();
        reservationTimeJdbcUtil.saveReservationTime(new ReservationTime(LocalTime.parse("10:00")));
        reservationTimeJdbcUtil.saveReservationTime(new ReservationTime(LocalTime.parse("11:00")));
        reservationTimeJdbcUtil.saveReservationTime(new ReservationTime(LocalTime.parse("12:00")));

        reservationJdbcUtil.saveReservationAsDateNow();

        RestAssured.given(spec)
                .param("date", LocalDate.now().toString())
                .param("theme-id", 1)
                .log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .filter(document(ROOT_IDENTIFIER + "/find-available",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestCookies(cookieWithName("token").description("로그인 유저 토큰")),
                        queryParameters(
                                parameterWithName("date").description("날짜"),
                                parameterWithName("theme-id").description("테마 식별자")
                        ),
                        responseFields(
                                fieldWithPath("resources[].id").type(JsonFieldType.NUMBER).description("식별자"),
                                fieldWithPath("resources[].startAt").type(JsonFieldType.STRING).description("시작 시간"),
                                fieldWithPath("resources[].booked").type(JsonFieldType.BOOLEAN).description("예약 여부")
                        )
                ))
                .when()
                .get("/times/available")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("resources.$", hasSize(3));
    }

    @DisplayName("시간 정보를 저장 성공 시 201 응답과 Location 헤더에 리소스 저장 경로를 받는다.")
    @Test
    void save() throws JsonProcessingException {
        TimeSaveRequest timeSaveRequest = new TimeSaveRequest(LocalTime.now());

        RestAssured.given().log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(timeSaveRequest))
                .accept(ContentType.JSON)
                .when()
                .post("/times")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", "/times/1");
    }

    @DisplayName("시간 삭제 성공시 204 응답을 받는다.")
    @Test
    void delete() {
        RestAssured.given().log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .delete("/times/{id}", 1L)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
