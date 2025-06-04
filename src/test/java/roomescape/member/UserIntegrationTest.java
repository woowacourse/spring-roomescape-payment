package roomescape.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.time.LocalDate;
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
import roomescape.global.auth.dto.CheckLoginResponse;
import roomescape.global.auth.dto.LoginRequest;
import roomescape.member.dto.request.SignupRequest;
import roomescape.member.dto.response.MemberResponse;
import roomescape.payment.infrastructure.TossApiClient;
import roomescape.payment.infrastructure.dto.response.PaymentResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.fixture.TestFixture;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.sql.init.data-locations=classpath:test-data.sql"
})
public class UserIntegrationTest {

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
    void step1_exceptionHandle() {
        String adminToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);
        Map<String, String> reservationTime = new HashMap<>();
        reservationTime.put("startAt", "10 00");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(reservationTime)
                .cookie(TOKEN, adminToken)
                .when().post("/times")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    void step2_createAndDeleteTheme() {
        String adminToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);
        createTheme("추리");
        findThemesBySize(1);

        RestAssured.given().log().all()
                .cookie(TOKEN, adminToken)
                .when().delete("/themes/1")
                .then().log().all()
                .statusCode(204);
        findThemesBySize(0);
    }

    @Test
    void step3_findAvailableReservations() {
        createReservationTime();
        createTheme("추리");

        LocalDate now = LocalDate.now();
        RestAssured.given().log().all()
                .when().queryParams("date", now.toString(), "themeId", 1L).get("/times/available")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void step3_findPopularTheme() {
        createReservationTime();
        createTheme("추리1");
        createTheme("추리2");
        createTheme("추리3");
        createTheme("추리4");
        createTheme("추리5");
        createTheme("추리6");
        createTheme("추리7");
        createTheme("추리8");
        createTheme("추리9");
        createTheme("추리10");
        createTheme("추리11");
        createTheme("추리12");
        findThemesBySize(12);

        RestAssured.given().log().all()
                .when().get("/themes/popular")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(10));
    }

    @Test
    void step4_signup() {
        RestAssured.given().log().all()
                .body(new SignupRequest("testuser@gmail.com", PASSWORD, "testUser"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/members")
                .then().log().all()
                .statusCode(201);

        loginAndGetAuthToken("testuser@gmail.com", PASSWORD);
    }

    @Test
    void step4_login() {
        RestAssured.given().log().all()
                .body(new LoginRequest(USER_EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/login")
                .then().log().all()
                .statusCode(200);
    }


    @Test
    void step4_logout() {
        String userToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);
        RestAssured.given().log().all()
                .body(new LoginRequest(USER_EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookie(TOKEN, userToken)
                .when().post("/logout")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    void step4_loginCheck() {
        String userToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);
        CheckLoginResponse checkLoginResponse = RestAssured.given().log().all()
                .body(new LoginRequest(USER_EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookie(TOKEN, userToken)
                .when().get("/login/check")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(CheckLoginResponse.class);

        assertThat(checkLoginResponse.name()).isEqualTo("User");
    }

    @Test
    void step5_admin_reservation() {
        String adminToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);
        createReservationTime();
        createTheme("추리");

        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", futureDate);
        reservation.put("timeId", 1);
        reservation.put("themeId", 1);
        reservation.put("memberId", 2);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(reservation)
                .cookie(TOKEN, adminToken)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    void step5_findAllUsers() {
        String adminToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);
        List<MemberResponse> memberResponses = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(TOKEN, adminToken)
                .when().get("/members")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });
        assertThat(memberResponses.size()).isEqualTo(1);
    }

    @Test
    void step6_responseUnAuthorizedWhenUserAccessAdminPage() {
        RestAssured.given().log().all()
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    void step6_responseForbiddenWhenUserAccessAdminPage() {
        String userToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);
        RestAssured.given().log().all()
                .cookie(TOKEN, userToken)
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(403);
    }

    @Test
    void step7_filterReservations() {
        createReservationTime();
        createTheme("추리");
        createTheme("로맨스");
        createUserReservation(1L);
        createUserReservation(2L);

        List<ReservationResponse> reservationsFilteredByThemeId = RestAssured.given().log().all()
                .when().queryParams("themeId", 1L, "memberId", 2L, "dateFrom", futureDate,
                        "dateTo", TestFixture.makeFutureDate().plusDays(1).toString())
                .get("/reservations")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });
        assertThat(reservationsFilteredByThemeId.size()).isEqualTo(1);
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

    private void findThemesBySize(final int size) {
        RestAssured.given().log().all()
                .when().get("/themes")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(size));
    }
}
