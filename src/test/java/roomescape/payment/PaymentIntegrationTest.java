package roomescape.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.global.auth.dto.LoginRequest;
import roomescape.payment.exception.TossPaymentClientException;
import roomescape.payment.exception.TossPaymentServerException;
import roomescape.payment.infrastructure.TossApiClient;
import roomescape.reservation.fixture.TestFixture;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.sql.init.data-locations=classpath:test-data.sql"
})
public class PaymentIntegrationTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }
    @MockitoBean
    private TossApiClient tossApiClient;

    private static final String USER_EMAIL = "user@gmail.com";
    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String PASSWORD = "password";
    private static final String futureDate = TestFixture.makeFutureDate().toString();
    private static final String TOKEN = "token";

    @Test
    void tossPaymentClientException() {
        when(tossApiClient.authPayment(eq("test_payment_key"), eq("test_order_id"), eq(50000), eq("CARD")))
                .thenThrow(new TossPaymentClientException("결제 승인이 거절되었습니다."));
        String expected = "결제 승인이 거절되었습니다.";
        createTheme("테마1");
        createReservationTime();
        String authToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);
        Map<String, Object> request = makeCreateReservationRequest();

        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .cookie(TOKEN, authToken)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .extract()
                .response();
        String actual = response.asString();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void tossPaymentServerException() {
        when(tossApiClient.authPayment(eq("test_payment_key"), eq("test_order_id"), eq(50000), eq("CARD")))
                .thenThrow(new TossPaymentServerException("내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요."));
        String expected = "내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요.";
        createTheme("테마1");
        createReservationTime();
        String authToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);
        Map<String, Object> request = makeCreateReservationRequest();

        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .cookie(TOKEN, authToken)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(500)
                .extract()
                .response();
        String actual = response.asString();
        assertThat(actual).isEqualTo(expected);
    }

    private static Map<String, Object> makeCreateReservationRequest() {
        Map<String, Object> reservation = makeReservationRequest();
        Map<String, Object> payment = makePaymentRequest();
        Map<String, Object> request = new HashMap<>();
        request.put("reservation", reservation);
        request.put("payment", payment);
        return request;
    }

    private static Map<String, Object> makePaymentRequest() {
        Map<String, Object> payment = new HashMap<>();
        payment.put("paymentKey", "test_payment_key");
        payment.put("orderId", "test_order_id");
        payment.put("amount", 50000);
        payment.put("paymentType", "CARD");
        return payment;
    }

    private static Map<String, Object> makeReservationRequest() {
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", futureDate);
        reservation.put("timeId", 1);
        reservation.put("themeId", 1);
        return reservation;
    }

    private static String loginAndGetAuthToken(final String email, final String password) {
        return RestAssured.given().log().all()
                .body(new LoginRequest(email, password))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .cookie(TOKEN);
    }


    private void createTheme(final String name) {
        String authToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);

        Map<String, String> theme = new HashMap<>();
        theme.put("name", name);
        theme.put("description", "셜록 with Danny");
        theme.put("thumbnail", "image.png");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(theme)
                .cookie(TOKEN, authToken)
                .when().post("/themes")
                .then().log().all()
                .statusCode(201);
    }

    private void createReservationTime() {
        String authToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);

        Map<String, String> reservationTime = new HashMap<>();
        reservationTime.put("startAt", "10:00");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(reservationTime)
                .cookie(TOKEN, authToken)
                .when().post("/times")
                .then().log().all()
                .statusCode(201);
    }


}
