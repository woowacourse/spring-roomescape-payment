package roomescape.reservation.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static roomescape.util.Fixture.TODAY;
import static roomescape.util.RestDocsFilter.CANCEL_RESERVATION_BY_USER;
import static roomescape.util.RestDocsFilter.CREATE_RESERVATION_BY_USER;
import static roomescape.util.RestDocsFilter.CREATE_WAITING;
import static roomescape.util.RestDocsFilter.DELETE_WAITING;
import static roomescape.util.RestDocsFilter.GET_MY_RESERVATION;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import roomescape.config.IntegrationTest;
import roomescape.exception.PaymentFailException;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.dto.ReservationSaveRequest;
import roomescape.util.CookieUtils;

class ReservationApiControllerTest extends IntegrationTest {

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("회원별 예약 목록 조회에 성공하면 200 응답을 받는다.")
    @Test
    void findMemberReservations() {
        saveMemberAsKaki();
        saveThemeAsHorror();
        saveReservationTimeAsTen();
        saveSuccessReservationAsDateTomorrow();

        RestAssured.given(spec).log().all()
                .filter(GET_MY_RESERVATION.getFilter())
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .get("/reservations/mine")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("responses", hasSize(1));
    }

    @DisplayName("회원이 예약을 성공적으로 추가하면 201 응답과 Location 헤더에 리소스 저장 경로를 받는다.")
    @Test
    void saveReservation() throws JsonProcessingException {
        saveMemberAsKaki();
        saveThemeAsHorror();
        saveReservationTimeAsTen();

        ReservationSaveRequest reservationSaveRequest
                = new ReservationSaveRequest(1L, TODAY, 1L, 1L, "testKey", "testId", 1000);

        PaymentResponse paymentResponse = new PaymentResponse("testKey", new BigDecimal("1000"));
        doReturn(paymentResponse).when(paymentService).pay(PaymentRequest.from(reservationSaveRequest), 1L);

        RestAssured.given(spec).log().all()
                .filter(CREATE_RESERVATION_BY_USER.getFilter())
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(reservationSaveRequest))
                .accept(ContentType.JSON)
                .when()
                .post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", "/reservations/1");
    }

    @DisplayName("회원이 결제에 실패하면 400 응답을 받는다.")
    @Test
    void failPaymentReservation() throws JsonProcessingException {
        saveMemberAsKaki();
        saveThemeAsHorror();
        saveReservationTimeAsTen();

        ReservationSaveRequest reservationSaveRequest
                = new ReservationSaveRequest(1L, TODAY, 1L, 1L, "testKey", "testId", 1000);
        doThrow(PaymentFailException.class).when(paymentService).pay(PaymentRequest.from(reservationSaveRequest), 1L);

        RestAssured.given().log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(reservationSaveRequest))
                .accept(ContentType.JSON)
                .when()
                .post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("회원이 예약 대기를 성공적으로 추가하면 201 응답과 Location 헤더에 리소스 저장 경로를 받는다.")
    @Test
    void saveReservationWaiting() throws JsonProcessingException {
        saveMemberAsKaki();
        saveThemeAsHorror();
        saveReservationTimeAsTen();

        ReservationSaveRequest reservationSaveRequest = new ReservationSaveRequest(TODAY, 1L, 1L);

        RestAssured.given(spec).log().all()
                .filter(CREATE_WAITING.getFilter())
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(reservationSaveRequest))
                .accept(ContentType.JSON)
                .when()
                .post("/reservations/waiting")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", "/reservations/1");
    }

    @DisplayName("예약을 성공적으로 취소하면 200 응답을 받는다.")
    @Test
    void cancelReservationByUser() {
        saveAdminMemberAsDuck();
        saveThemeAsHorror();
        saveReservationTimeAsTen();
        saveSuccessReservationAsDateTomorrow();

        RestAssured.given(spec).log().all()
                .filter(CANCEL_RESERVATION_BY_USER.getFilter())
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .patch("/reservations/{id}", 1L)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("예약 대기를 성공적으로 제거하면 204 응답을 받는다.")
    @Test
    void delete() {
        saveAdminMemberAsDuck();
        saveMemberAsKaki();
        saveThemeAsHorror();
        saveReservationTimeAsTen();
        saveSuccessReservationAsDateTomorrow();
        saveWaitingAsDateTomorrow();

        RestAssured.given(spec).log().all()
                .filter(DELETE_WAITING.getFilter())
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .delete("/reservations/{id}", 1L)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
