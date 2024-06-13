package roomescape.payment.controller;

import static org.hamcrest.Matchers.hasSize;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import roomescape.common.config.ControllerTest;
import roomescape.common.util.CookieUtils;

class PaymentControllerTest extends ControllerTest {

    @DisplayName("결제 목록 조회에 성공하면 200 응답을 받는다.")
    @Test
    void findAll() {
        memberJdbcUtil.saveMemberAsKaki();
        themeJdbcUtil.saveThemeAsHorror();
        reservationTimeJdbcUtil.saveReservationTimeAsTen();
        reservationJdbcUtil.saveReservationAsDateNow();
        paymentJdbcUtil.savePayment();

        RestAssured.given().log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .get("/payments")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("resources.$", hasSize(1));
    }
}
