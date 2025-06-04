package roomescape.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.global.auth.dto.LoginRequest;
import roomescape.payment.infrastructure.TossApiClient;
import roomescape.payment.infrastructure.dto.response.PaymentResponse;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.dto.response.MyReservationResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.fixture.TestFixture;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.sql.init.data-locations=classpath:test-data.sql"
})
public class ReservationIntegrationTest {

    @LocalServerPort
    int port;

    @MockitoBean
    private TossApiClient tossApiClient;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        PaymentResponse mockResponse = new PaymentResponse(
                "test_payment_key",
                "test_order_id",
                "CARD",
                50000,
                "DONE",
                OffsetDateTime.of(2025, 5, 28, 20, 48, 23, 0, ZoneOffset.UTC)
        );
        when(tossApiClient.authPayment(any())).thenReturn(mockResponse);
    }

    private static final String USER_EMAIL = "user@gmail.com";
    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String PASSWORD = "password";
    private static final String futureDate = TestFixture.makeFutureDate().toString();
    private static final String TOKEN = "token";

    @Test
    void step1_findMyReservations() {
        String userToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);
        createReservationTime();
        createTheme("추리");
        createUserReservation(1L);

        List<MyReservationResponse> responses = RestAssured.given().log().all()
                .cookie(TOKEN, userToken)
                .when().get("/reservations-mine")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        assertThat(responses.size()).isEqualTo(1);
    }

    @Test
    void step3_createWaiting() {
        String userToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);
        createReservationTime();
        createTheme("추리");
        createUserReservation(1L);   // RESERVED
        createUserWaiting(1L);   // WAITING

        RestAssured.given().log().all()
                .cookie(TOKEN, userToken)
                .when().get("/waiting")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void step3_findMyReservations() {
        String userToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);
        createReservationTime();
        createTheme("추리");
        createUserReservation(1L);   // RESERVED
        createUserWaiting(1L);   // WAITING

        RestAssured.given().log().all()
                .cookie(TOKEN, userToken)
                .when().get("/reservations-mine")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }

    @Test
    void step3_findMyReservationWithPayment() {
        String userToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);
        createReservationTime();
        createTheme("추리");
        createUserReservation(1L);   // RESERVED

        RestAssured.given().log().all()
                .cookie(TOKEN, userToken)
                .when().get("/reservations-mine")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].theme", equalTo("추리"))
                .body("[0].date", equalTo(futureDate))
                .body("[0].time", equalTo("10:00:00"))
                .body("[0].reservedStatus", equalTo(ReservationStatus.RESERVED.getName()))
                .body("[0].paymentKey", equalTo("test_payment_key"))
                .body("[0].amount", equalTo(50000));
    }

    @Test
    void step3_deleteWaiting() {
        String userToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);
        createReservationTime();
        createTheme("추리");
        createUserReservation(1L);   // RESERVED
        createUserWaiting(1L);   // WAITING

        Long waitingId = RestAssured.given().log().all()
                .cookie(TOKEN, userToken)
                .when().get("/waiting")
                .then().extract().jsonPath().getLong("[0].id");

        RestAssured.given().log().all()
                .cookie(TOKEN, userToken)
                .when().delete("/waiting/" + waitingId)
                .then().log().all()
                .statusCode(204);

        RestAssured.given().log().all()
                .when().get("/waiting")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void step4_findWaiting() {
        createReservationTime();
        createTheme("추리");
        createUserReservation(1L);
        createUserWaiting(1L);

        RestAssured.given().log().all()
                .cookie(TOKEN, loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD))
                .when().get("/waiting")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void step4_cancelReservation_promotesWaiting() {
        createReservationTime();
        createTheme("추리");
        createUserReservation(1L);
        createUserWaiting(1L);

        String adminToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);

        List<ReservationResponse> before = RestAssured.given().log().all()
                .cookie(TOKEN, adminToken)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .extract().as(new TypeRef<List<ReservationResponse>>() {
                });
        assertThat(before).hasSize(1);
        Long reservedId = before.get(0).id();

        RestAssured.given().log().all()
                .cookie(TOKEN, adminToken)
                .when().delete("/reservations/" + reservedId)
                .then().log().all()
                .statusCode(204);

        List<ReservationResponse> after = RestAssured.given().log().all()
                .cookie(TOKEN, adminToken)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .extract().as(new TypeRef<List<ReservationResponse>>() {
                });
        assertThat(after).hasSize(1);

        RestAssured.given().log().all()
                .cookie(TOKEN, adminToken)
                .when().get("/waiting")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }

    private String loginAndGetAuthToken(final String email, final String password) {
        return RestAssured.given().log().all()
                .body(new LoginRequest(email, password))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .cookie(TOKEN);
    }

    private void createUserReservation(final Long themeId) {
        String authToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);

        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", futureDate);
        reservation.put("timeId", 1);
        reservation.put("themeId", themeId);

        Map<String, Object> payment = new HashMap<>();
        payment.put("paymentKey", "test_payment_key");
        payment.put("orderId", "test_order_id");
        payment.put("amount", 50000);
        payment.put("paymentType", "CARD");

        Map<String, Object> request = new HashMap<>();
        request.put("reservation", reservation);
        request.put("payment", payment);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .cookie(TOKEN, authToken)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);
    }

    private void createUserWaiting(final Long themeId) {
        String authToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);

        Map<String, Object> waiting = new HashMap<>();
        waiting.put("date", futureDate);
        waiting.put("timeId", 1);
        waiting.put("themeId", themeId);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(waiting)
                .cookie(TOKEN, authToken)
                .when().post("/waiting")
                .then().log().all()
                .statusCode(201);
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
