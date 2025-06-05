package roomescape.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.e2e.fixture.TestFixtureE2e;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.payment.infrastructure.TossApiClient;
import roomescape.reservation.controller.ReservationController;
import roomescape.reservation.fixture.TestFixture;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.sql.init.data-locations=classpath:test-data.sql"
})
public class AdminTest {

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
        when(tossApiClient.authPayment(any())).thenReturn(mockResponse);
    }

    @Test
    void accessAdminPage() {
        String authToken = TestFixtureE2e.loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);

        RestAssured.given().log().all()
                .cookie(TOKEN, authToken)
                .when().get("/admin")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void step2_ccessAdminReservationPage() {
        String authToken = TestFixtureE2e.loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);

        RestAssured.given().log().all()
                .cookie(TOKEN, authToken)
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void createAndDeleteReservation() {
        String authToken = TestFixtureE2e.loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);

        TestFixtureE2e.createReservationTime();
        TestFixtureE2e.createTheme("추리");
        TestFixtureE2e.createUserReservation(1L);

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("data.size()", is(1));

        RestAssured.given().log().all()
                .cookie(TOKEN, authToken)
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(204);

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("data.size()", is(0));
    }

    @Test
    void applyDatabase() {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.getCatalog()).isEqualTo("DATABASE");
            assertThat(connection.getMetaData().getTables(null, null, "RESERVATION", null).next()).isTrue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getReservationWithDatabase() {
        TestFixtureE2e.createReservationTime();
        TestFixtureE2e.createTheme("추리");

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
    void addReservationWithDatabase() {
        String authToken = TestFixtureE2e.loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);

        TestFixtureE2e.createReservationTime();
        TestFixtureE2e.createTheme("추리");
        TestFixtureE2e.createUserReservation(1L);

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
    void timeAPIFeature() {
        String authToken = TestFixtureE2e.loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);
        TestFixtureE2e.createReservationTime();

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
    void schemaModification() {
        TestFixtureE2e.createReservationTime();
        TestFixtureE2e.createTheme("추리");
        TestFixtureE2e.createUserReservation(1L);

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void controllerDoesNotHasDatabaseLogic() {
        boolean isJdbcTemplateInjected = false;

        for (Field field : reservationController.getClass().getDeclaredFields()) {
            if (field.getType().equals(JdbcTemplate.class)) {
                isJdbcTemplateInjected = true;
                break;
            }
        }
        assertThat(isJdbcTemplateInjected).isFalse();
    }

    @Test
    void admin_reservation() {
        String authToken = TestFixtureE2e.loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);
        TestFixtureE2e.createReservationTime();
        TestFixtureE2e.createTheme("추리");

        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", futureDate);
        reservation.put("timeId", 1);
        reservation.put("themeId", 1);
        reservation.put("memberId", 2);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(reservation)
                .cookie(TOKEN, authToken)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    void indAllUsers() {
        String authToken = TestFixtureE2e.loginAndGetAuthToken(USER_EMAIL, PASSWORD);
        RestAssured.given().log().all()
                .cookie(TOKEN, authToken)
                .when().get("/members")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }
}
