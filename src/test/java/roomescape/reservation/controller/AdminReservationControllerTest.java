package roomescape.reservation.controller;

import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static roomescape.Fixture.TOMORROW;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import roomescape.common.config.IntegrationTest;
import roomescape.common.util.CookieUtils;
import roomescape.reservation.controller.dto.request.ReservationSaveRequest;

class AdminReservationControllerTest extends IntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("관리자가 예약을 성공적으로 추가하면 201 응답과 Location 헤더에 리소스 저장 경로를 받는다.")
    @Test
    void saveAdminReservation() throws JsonProcessingException {
        saveAdminMember();
        saveMemberAsKaki();
        saveThemeAsHorror();
        saveReservationTimeAsTen();

        ReservationSaveRequest adminRequest = new ReservationSaveRequest(2L, TOMORROW, 1L, 1L, null, null);

        RestAssured.given(this.spec).log().all()
                .cookie(CookieUtils.TOKEN_KEY, getAdminToken())
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(adminRequest))
                .accept(ContentType.JSON)
                .filter(document("reservations/admin/save"))
                .when()
                .post("/admin/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", "/reservations/1");
    }

    @DisplayName("이미 동일한 예약을 한 회원이 중복된 예약을 시도하면 400 에러를 반환한다.")
    @Test
    void failWhenSameMemberReserves() throws JsonProcessingException {
        saveAdminMember();
        saveMemberAsKaki();
        saveThemeAsHorror();
        saveReservationTimeAsTen();
        saveSuccessReservationAsDateNow();

        ReservationSaveRequest adminRequest = new ReservationSaveRequest(1L, LocalDate.now(), 1L, 1L, null, null);

        RestAssured.given(this.spec).log().all()
                .cookie(CookieUtils.TOKEN_KEY, getAdminToken())
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(adminRequest))
                .accept(ContentType.JSON)
                .filter(document("reservations/admin/save/fail/duplicated"))
                .when()
                .post("/admin/reservations")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("이미 예약이 완료된 예약을 시도한다면 400 에러를 반환한다.")
    @Test
    void failWhenTryToMakeReservationAlreadyReserved() throws JsonProcessingException {
        saveAdminMember();
        saveMemberAsKaki();
        saveMemberAsAnna();
        saveThemeAsHorror();
        saveReservationTimeAsTen();
        saveSuccessReservationAsDateNow();

        ReservationSaveRequest adminRequest = new ReservationSaveRequest(2L, LocalDate.now(), 1L, 1L, null, null);

        RestAssured.given(this.spec).log().all()
                .cookie(CookieUtils.TOKEN_KEY, getAdminToken())
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(adminRequest))
                .accept(ContentType.JSON)
                .filter(document("reservations/admin/save/fail/already-reserved"))
                .when()
                .post("/admin/reservations")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
