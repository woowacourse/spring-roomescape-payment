package roomescape.e2e.fixture;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.global.auth.dto.LoginRequest;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.payment.infrastructure.TossApiClient;
import roomescape.reservation.controller.ReservationController;
import roomescape.reservation.fixture.TestFixture;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.sql.init.data-locations=classpath:test-data.sql"
})
public class TestFixtureE2e {

    @LocalServerPort
    int port;

    @MockitoBean
    private TossApiClient tossApiClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReservationController reservationController;

    private static final String USER_EMAIL = "user@gmail.com";
    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String PASSWORD = "password";
    private static final String futureDate = TestFixture.makeFutureDate().toString();
    private static final String TOKEN = "token";

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        PaymentResponse mockResponse = new PaymentResponse(
                "test_payment_key",
                "test_order_id",
                "CARD",
                50000,
                "DONE",
                "2025-05-28T20:48:23+09:00"
        );
        when(tossApiClient.authPayment(any(), any(), any(), any())).thenReturn(mockResponse);
    }


    public static String loginAndGetAuthToken(final String email, final String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().post("/login")
                .then().log().all()
                .extract().cookie(TOKEN);
    }

    public static void createUserReservation(final Long themeId) {
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
    public static void createTheme(final String name ) {
        Map<String, String> theme = new HashMap<>();
        theme.put("name", name);
        theme.put("description", "테마 설명");
        theme.put("thumbnail", "https://example.com/thumbnail.jpg");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(TOKEN, loginAndGetAuthToken(ADMIN_EMAIL,PASSWORD))
                .body(theme)
                .when().post("/themes")
                .then().log().all()
                .statusCode(201);
    }

    public static void createReservationTime() {
        Map<String, String> reservationTime = new HashMap<>();
        reservationTime.put("startAt", "10:00");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(TOKEN, loginAndGetAuthToken(ADMIN_EMAIL,PASSWORD))
                .body(reservationTime)
                .when().post("/times")
                .then().log().all()
                .statusCode(201);
    }

    public static void findThemesBySize(final int size) {
        RestAssured.given().log().all()
                .when().get("/themes")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(size));
    }

public static void createUserWaiting(final Long themeId) {
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
}
