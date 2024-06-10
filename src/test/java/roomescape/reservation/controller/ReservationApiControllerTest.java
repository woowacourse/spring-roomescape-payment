package roomescape.reservation.controller;

import static roomescape.util.Fixture.ORDER_ID;
import static roomescape.util.Fixture.PAYMENT_KEY;
import static roomescape.util.Fixture.TODAY;

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
import roomescape.payment.service.PaymentService;
import roomescape.reservation.dto.UserReservationSaveRequest;
import roomescape.util.CookieUtils;

class ReservationApiControllerTest extends IntegrationTest {

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("예약 목록 조회에 성공하면 200 응답을 받는다.")
    @Test
    void findAll() {
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
    void findMemberReservations() {
        RestAssured.given().log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .get("/reservations/mine")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("회원이 예약을 성공적으로 추가하면 201 응답과 Location 헤더에 리소스 저장 경로를 받는다.")
    @Test
    void saveReservation() throws JsonProcessingException {
        saveMemberAsKaki();
        saveThemeAsHorror();
        saveReservationTimeAsTen();

        UserReservationSaveRequest userReservationSaveRequest
                = new UserReservationSaveRequest(TODAY, 1L, 1L, PAYMENT_KEY, ORDER_ID, BigDecimal.valueOf(1000));

        RestAssured.given().log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(userReservationSaveRequest))
                .accept(ContentType.JSON)
                .when()
                .post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", "/reservations/1");
    }

    @DisplayName("회원이 예약 대기를 성공적으로 추가하면 201 응답과 Location 헤더에 리소스 저장 경로를 받는다.")
    @Test
    void saveReservationWaiting() throws JsonProcessingException {
        saveMemberAsKaki();
        saveThemeAsHorror();
        saveReservationTimeAsTen();

        UserReservationSaveRequest userReservationSaveRequest
                = new UserReservationSaveRequest(TODAY, 1L, 1L, null, null, null);

        RestAssured.given().log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(userReservationSaveRequest))
                .accept(ContentType.JSON)
                .when()
                .post("/reservations/waiting")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", "/reservations/1");
    }

    @DisplayName("예약을 성공적으로 제거하면 204 응답을 받는다.")
    @Test
    void delete() {
        RestAssured.given().log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .delete("/reservations/{id}", 1L)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
