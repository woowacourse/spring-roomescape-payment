package roomescape.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.global.auth.dto.LoginRequest;
import roomescape.payment.infrastructure.TossApiClient;
import roomescape.payment.infrastructure.dto.response.PaymentResponse;
import roomescape.reservation.controller.ReservationController;
import roomescape.reservation.fixture.TestFixture;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.sql.init.data-locations=classpath:test-data.sql"
})
public class AdminIntegrationTest {

    @LocalServerPort
    int port;

    @MockitoBean
    private TossApiClient tossApiClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReservationController reservationController;

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
    void step1_accessAdminPage() {
        String authToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);

        RestAssured.given().log().all()
                .cookie(TOKEN, authToken)
                .when().get("/admin")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void step2_accessAdminReservationPage() {
        String authToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);

        RestAssured.given().log().all()
                .cookie(TOKEN, authToken)
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void step3_createAndDeleteReservation() {
        String authToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);

        createReservationTime();
        createTheme("추리");
        createUserReservation(1L);

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));

        RestAssured.given().log().all()
                .cookie(TOKEN, authToken)
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(204);

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void step4_applyDatabase() {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.getCatalog()).isEqualTo("DATABASE");
            assertThat(connection.getMetaData().getTables(null, null, "RESERVATION", null).next()).isTrue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void step5_getReservationWithDatabase() {
        createReservationTime();
        createTheme("추리");

        jdbcTemplate.update(
                "INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES (?, ?, ?, ?)",
                futureDate, "1", "1", "1");

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200);

        Integer count = jdbcTemplate.queryForObject("SELECT count(1) from reservation", Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void step6_addReservationWithDatabase() {
        String authToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);

        createReservationTime();
        createTheme("추리");
        createUserReservation(1L);

        Integer count = jdbcTemplate.queryForObject("SELECT count(1) from reservation", Integer.class);
        assertThat(count).isEqualTo(1);

        RestAssured.given().log().all()
                .cookie(TOKEN, authToken)
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(204);

        Integer countAfterDelete = jdbcTemplate.queryForObject("SELECT count(1) from reservation", Integer.class);
        assertThat(countAfterDelete).isEqualTo(0);
    }

    @Test
    void step7_timeAPIFeature() {
        String authToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);
        createReservationTime();

        RestAssured.given().log().all()
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));

        RestAssured.given().log().all()
                .cookie(TOKEN, authToken)
                .when().delete("/times/1")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    void step8_schemaModification() {
        createReservationTime();
        createTheme("추리");
        createUserReservation(1L);

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void step9_controllerDoesNotHasDatabaseLogic() {
        boolean isJdbcTemplateInjected = false;

        for (Field field : reservationController.getClass().getDeclaredFields()) {
            if (field.getType().equals(JdbcTemplate.class)) {
                isJdbcTemplateInjected = true;
                break;
            }
        }
        assertThat(isJdbcTemplateInjected).isFalse();
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
