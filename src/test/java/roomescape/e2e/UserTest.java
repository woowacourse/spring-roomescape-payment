package roomescape.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static roomescape.e2e.fixture.TestFixtureE2e.loginAndGetAuthToken;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.e2e.fixture.TestFixtureE2e;
import roomescape.global.auth.dto.CheckLoginResponse;
import roomescape.global.auth.dto.LoginRequest;
import roomescape.member.dto.request.SignupRequest;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.payment.infrastructure.TossApiClient;
import roomescape.reservation.dto.response.MyReservationResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.fixture.TestFixture;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.sql.init.data-locations=classpath:test-data.sql"
})
public class UserTest {

    @MockitoBean
    private TossApiClient tossApiClient;

    private static final String USER_EMAIL = "user@gmail.com";
    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String PASSWORD = "password";
    private static final String futureDate = TestFixture.makeFutureDate().toString();
    private static final String TOKEN = "token";


    @LocalServerPort
    int port;


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

    @Test
    void findAvailableReservations() {
        String authToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);

        TestFixtureE2e.createReservationTime();
        TestFixtureE2e.createTheme("추리");

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void findPopularTheme() {
        String authToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);

        TestFixtureE2e.createReservationTime();
        TestFixtureE2e.createTheme("추리");
        TestFixtureE2e.createUserReservation(1L);

        RestAssured.given().log().all()
                .when().get("/themes/popular")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void signup() {
        SignupRequest signupRequest = new SignupRequest("newuser@gmail.com", "password", "newuser");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(signupRequest)
                .when().post("/members")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    void login() {
        LoginRequest loginRequest = new LoginRequest(USER_EMAIL, PASSWORD);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().post("/login")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void logout() {
        String authToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);
        RestAssured.given().log().all()
                .cookie(TOKEN, authToken)
                .when().post("/logout")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    void loginCheck() {
        String authToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);
        CheckLoginResponse checkLoginResponse = RestAssured.given().log().all()
                .body(new LoginRequest(USER_EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookie(TOKEN, authToken)
                .when().get("/login/check")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(CheckLoginResponse.class);

        assertThat(checkLoginResponse.name()).isEqualTo("User");
    }


    @Test
    void responseUnAuthorizedWhenUserAccessAdminPage() {
        RestAssured.given().log().all()
                .when().get("/admin")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    void responseForbiddenWhenUserAccessAdminPage() {
        String authToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);

        RestAssured.given().log().all()
                .cookie(TOKEN, authToken)
                .when().get("/admin")
                .then().log().all()
                .statusCode(403);
    }

    @Test
    void filterReservations() {
        String authToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);

        TestFixtureE2e.createReservationTime();
        TestFixtureE2e.createTheme("추리");
        TestFixtureE2e.createUserReservation(1L);

        RestAssured.given().log().all()
                .param("date", futureDate)
                .param("themeId", 1)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }


    @Test
    void findMyReservations() {
        String authToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);

        TestFixtureE2e.createReservationTime();
        TestFixtureE2e.createTheme("추리");
        TestFixtureE2e.createUserReservation(1L);

        List<MyReservationResponse> responses = RestAssured.given().log().all()
                .cookie(TOKEN, authToken)
                .when().get("/reservations-mine")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        assertThat(responses.size()).isEqualTo(1);
    }

    @Test
    void createWaiting() {
        String authToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);

        TestFixtureE2e.createReservationTime();
        TestFixtureE2e.createTheme("추리");
        TestFixtureE2e.createUserReservation(1L);   // RESERVED
        TestFixtureE2e.createUserWaiting(1L);   // WAITING

        RestAssured.given().log().all()
                .cookie(TOKEN, authToken)
                .when().get("/waiting")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void findMyReservationsWithWaiting() {
        String authToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);

        TestFixtureE2e.createReservationTime();
        TestFixtureE2e.createTheme("추리");
        TestFixtureE2e.createUserReservation(1L);   // RESERVED
        TestFixtureE2e.createUserWaiting(1L);   // WAITING

        RestAssured.given().log().all()
                .cookie(TOKEN, authToken)
                .when().get("/reservations-mine")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }

    @Test
    void deleteWaiting() {
        String authToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);

        TestFixtureE2e.createReservationTime();
        TestFixtureE2e.createTheme("추리");
        TestFixtureE2e.createUserReservation(1L);   // RESERVED
        TestFixtureE2e.createUserWaiting(1L);   // WAITING

        Long waitingId = RestAssured.given().log().all()
                .cookie(TOKEN, authToken)
                .when().get("/waiting")
                .then().extract().jsonPath().getLong("[0].id");

        RestAssured.given().log().all()
                .cookie(TOKEN, authToken)
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
    void findWaiting() {
        String authToken = loginAndGetAuthToken(USER_EMAIL, PASSWORD);

        TestFixtureE2e.createReservationTime();
        TestFixtureE2e.createTheme("추리");
        TestFixtureE2e.createUserReservation(1L);
        TestFixtureE2e.createUserWaiting(1L);

        RestAssured.given().log().all()
                .cookie(TOKEN, authToken)
                .when().get("/waiting")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void cancelReservation_promotesWaiting() {

        TestFixtureE2e.createReservationTime();
        TestFixtureE2e.createTheme("추리");
        TestFixtureE2e.createUserReservation(1L);
        TestFixtureE2e.createUserWaiting(1L);

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
}
