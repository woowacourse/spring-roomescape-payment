package roomescape.reservation.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import roomescape.RestClientControllerTest;
import roomescape.auth.token.TokenProvider;
import roomescape.fixture.PaymentConfirmFixtures;
import roomescape.member.model.MemberRole;
import roomescape.payment.infrastructure.PaymentGateway;
import roomescape.reservation.dto.SaveReservationRequest;

class ReservationControllerTest extends RestClientControllerTest {

    @Autowired
    private TokenProvider tokenProvider;

    @MockBean
    private PaymentGateway paymentGateway;

    @DisplayName("예약 정보를 저장한다.")
    @Test
    @Sql("classpath:test-payment-credential-data.sql")
    void saveReservationTest() {
        final String orderId = "orderId";
        final long amount = 1000L;
        final String paymentKey = "paymentKey";
        final SaveReservationRequest saveReservationRequest = new SaveReservationRequest(
                LocalDate.now().plusDays(1),
                1L,
                1L,
                orderId,
                amount,
                paymentKey
        );
        given(paymentGateway.confirm(anyString(), anyLong(), anyString()))
                .willReturn(PaymentConfirmFixtures.getDefaultResponse(orderId, paymentKey, amount));

        RestAssured.given(spec).log().all()
                .filter(document("save-reservation"))
                .contentType(ContentType.JSON)
                .cookie("token", createUserAccessToken())
                .body(saveReservationRequest)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .body("id", is(17));
    }

    @DisplayName("존재하지 않는 예약 시간을 포함한 예약 저장 요청을 하면 400코드가 응답된다.")
    @Test
    void saveReservationWithNoExistReservationTime() {
        final SaveReservationRequest saveReservationRequest = new SaveReservationRequest(
                LocalDate.now().plusDays(1),
                80L,
                1L,
                "orderId",
                1000L,
                "paymentKey"
        );

        RestAssured.given(spec).log().all()
                .filter(document("fail-save-reservation-non-exist-reservation-time"))
                .contentType(ContentType.JSON)
                .cookie("token", createAdminAccessToken())
                .body(saveReservationRequest)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body("message", is("해당 id의 예약 시간이 존재하지 않습니다."));
    }

    @DisplayName("현재 날짜보다 이전 날짜의 예약을 저장하려고 요청하면 400코드가 응답된다.")
    @Test
    void saveReservationWithReservationDateAndTimeBeforeNow() {
        final SaveReservationRequest saveReservationRequest = new SaveReservationRequest(
                LocalDate.now().minusDays(1),
                1L,
                1L,
                "orderId",
                1000L,
                "paymentKey"
        );

        RestAssured.given(spec).log().all()
                .filter(document("fail-save-reservation-past-date"))
                .contentType(ContentType.JSON)
                .cookie("token", createAdminAccessToken())
                .body(saveReservationRequest)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body("message", is("현재 날짜보다 이전 날짜를 예약할 수 없습니다."));
    }

    @DisplayName("내 예약 정보를 조회한다.")
    @Test
    void getMyReservations() {
        RestAssured.given(spec).log().all()
                .filter(document("find-All-my-reservations"))
                .cookie("token", createAdminAccessToken())
                .when().get("/reservations-mine")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(3));
    }

    private String createUserAccessToken() {
        return tokenProvider.createToken(3L, MemberRole.USER);
    }

    private String createAdminAccessToken() {
        return tokenProvider.createToken(1L, MemberRole.ADMIN);
    }
}
