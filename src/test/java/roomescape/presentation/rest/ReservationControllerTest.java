package roomescape.presentation.rest;

import static org.mockito.BDDMockito.given;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import roomescape.application.request.PaymentInfo;
import roomescape.application.response.PaymentClientResponse;
import roomescape.infrastructure.payment.PaymentClient;
import roomescape.infrastructure.payment.toss.TossPaymentErrorCode;
import roomescape.infrastructure.payment.toss.TossPaymentException;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationControllerTest {

    private static final Map<String, String> RESERVATION_BODY = Map.of("date", "3000-03-17", "timeId", "1", "themeId",
            "1", "memberId", "2", "paymentKey", "paymentKey", "orderId", "orderId", "amount", "1000");

    @LocalServerPort
    private int port;

    @MockBean
    private PaymentClient paymentClient;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("예약 추가 요청시, id를 포함한 예약 내용과 CREATED를 응답한다")
    void createReservation() {
        PaymentInfo paymentInfo = new PaymentInfo("paymentKey", "orderId", 1000);
        PaymentClientResponse response = new PaymentClientResponse("paymentKey", "orderId", "테스트 방탈출 예약 결제 1건", 1000);

        given(paymentClient.confirmPayment(paymentInfo)).willReturn(response);

        var token = RestAssured.given().contentType(ContentType.JSON)
                .body(Map.of("email", "user1@email.com", "password", "password1")).when().post("/login").then()
                .statusCode(200).extract().response().getDetailedCookies().getValue("token");

        RestAssured.given().log().all().contentType(ContentType.JSON).cookie("token", token).body(RESERVATION_BODY)
                .when().post("/reservations").then().log().all().statusCode(HttpStatus.CREATED.value())
                .body("date", Matchers.equalTo("3000-03-17"));
    }

    @EnumSource(TossPaymentErrorCode.class)
    @ParameterizedTest
    @DisplayName("예약 추가 요청시, 결제에 실패하면 실패 에러 응답 코드를 반환한다")
    void createReservation_WhenPaymentFailed(TossPaymentErrorCode errorCode) {
        PaymentInfo paymentInfo = new PaymentInfo("paymentKey", "orderId", 1000);

        given(paymentClient.confirmPayment(paymentInfo)).willThrow(new TossPaymentException(errorCode));

        var token = RestAssured.given().contentType(ContentType.JSON)
                .body(Map.of("email", "user1@email.com", "password", "password1")).when().post("/login").then()
                .statusCode(200).extract().response().getDetailedCookies().getValue("token");

        RestAssured.given().log().all().contentType(ContentType.JSON).cookie("token", token).body(RESERVATION_BODY)
                .when().post("/reservations").then().log().all().statusCode(errorCode.getHttpStatus().value())
                .body("message", Matchers.equalTo(errorCode.getMessage()));
    }

    @Test
    @DisplayName("예약 조회 요청시, 존재하는 모든 예약과 OK를 응답한다")
    void findReservations() {
        RestAssured.given().log().all().when().get("/reservations").then().log().all().statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.is(3));
    }

    @Test
    @DisplayName("예약 삭제 요청시, 주어진 아이디에 해당하는 예약이 없다면 NOT FOUND를 응답한다.")
    void removeReservation_WhenReservationDoesNotExisted() {
        RestAssured.given().log().all().when().delete("/reservations/1000").then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("예약 삭제 요청시, 주어진 아이디에 해당하는 예약이 있다면 삭제하고 NO CONTENT를 응답한다.")
    void removeReservation() {
        RestAssured.given().log().all().when().delete("/reservations/1").then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
