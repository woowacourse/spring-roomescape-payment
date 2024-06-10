package roomescape.acceptance;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static roomescape.fixture.TestFixture.ADMIN_EMAIL;
import static roomescape.fixture.TestFixture.AMOUNT;
import static roomescape.fixture.TestFixture.DATE_MAY_EIGHTH;
import static roomescape.fixture.TestFixture.MEMBER_PASSWORD;
import static roomescape.fixture.TestFixture.MEMBER_TENNY_EMAIL;
import static roomescape.fixture.TestFixture.ORDER_ID;
import static roomescape.fixture.TestFixture.PAYMENT_KEY;
import static roomescape.fixture.TestFixture.START_AT_SIX;
import static roomescape.fixture.TestFixture.THEME_HORROR_DESCRIPTION;
import static roomescape.fixture.TestFixture.THEME_HORROR_NAME;
import static roomescape.fixture.TestFixture.THEME_HORROR_THUMBNAIL;
import static roomescape.fixture.TestFixture.paymentConfirmResponseFixture;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import roomescape.component.TossPaymentClient;
import roomescape.dto.auth.TokenRequest;
import roomescape.dto.auth.TokenResponse;
import roomescape.dto.reservation.ReservationSaveRequest;
import roomescape.dto.reservation.ReservationTimeSaveRequest;
import roomescape.dto.reservation.ReservationWaitingSaveRequest;
import roomescape.dto.theme.ThemeSaveRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
abstract class AcceptanceTest {

    @LocalServerPort
    private int port;

    @MockBean
    TossPaymentClient paymentClient;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        when(paymentClient.confirm(any())).thenReturn(paymentConfirmResponseFixture());
    }

    protected Long saveReservationTime() {
        final ReservationTimeSaveRequest request = new ReservationTimeSaveRequest(START_AT_SIX);

        final Integer id = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/times")
                .then().log().all()
                .statusCode(201)
                .extract()
                .jsonPath().get("id");

        return Long.valueOf(id);
    }

    protected Long saveTheme() {
        final ThemeSaveRequest request
                = new ThemeSaveRequest(THEME_HORROR_NAME, THEME_HORROR_DESCRIPTION, THEME_HORROR_THUMBNAIL);

        final Integer id = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/themes")
                .then().log().all()
                .statusCode(201)
                .extract()
                .jsonPath().get("id");

        return Long.valueOf(id);
    }

    protected Long saveReservation(final Long timeId, final Long themeId, final String email) {
        final String accessToken = getAccessToken(email);
        final ReservationSaveRequest request
                = new ReservationSaveRequest(DATE_MAY_EIGHTH, timeId, themeId, PAYMENT_KEY, ORDER_ID, AMOUNT);

        final Integer id = RestAssured.given().log().all()
                .cookie("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .extract()
                .jsonPath().get("id");

        return Long.valueOf(id);
    }

    protected Long saveReservationWaiting(final Long timeId, final Long themeId) {
        final String accessToken = getAccessToken(MEMBER_TENNY_EMAIL);
        final ReservationWaitingSaveRequest request
                = new ReservationWaitingSaveRequest(DATE_MAY_EIGHTH, timeId, themeId);

        final Integer id = RestAssured.given().log().all()
                .cookie("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(201)
                .extract()
                .jsonPath().get("id");

        return Long.valueOf(id);
    }

    protected Long saveReservationAndWaiting(final Long timeId, final Long themeId) {
        final ReservationSaveRequest reservationRequest
                = new ReservationSaveRequest(DATE_MAY_EIGHTH, timeId, themeId, PAYMENT_KEY, ORDER_ID, AMOUNT);
        RestAssured.given().log().all()
                .cookie("token", getAccessToken(ADMIN_EMAIL))
                .contentType(ContentType.JSON)
                .body(reservationRequest)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);

        final ReservationWaitingSaveRequest waitingRequest
                = new ReservationWaitingSaveRequest(DATE_MAY_EIGHTH, timeId, themeId);
        final Integer id = RestAssured.given().log().all()
                .cookie("token", getAccessToken(MEMBER_TENNY_EMAIL))
                .contentType(ContentType.JSON)
                .body(waitingRequest)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(201)
                .extract()
                .jsonPath().get("id");

        return Long.valueOf(id);
    }

    protected String getAccessToken(final String email) {
        return RestAssured.given().log().all()
                .body(new TokenRequest(email, MEMBER_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/login")
                .then().log().all()
                .extract().as(TokenResponse.class).accessToken();
    }

    protected ValidatableResponse assertPostResponseWithToken(final Object request, final String email,
            final String path, final int statusCode
    ) {
        final String accessToken = getAccessToken(email);
        return RestAssured.given().log().all()
                .cookie("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post(path)
                .then().log().all()
                .statusCode(statusCode);
    }

    protected ValidatableResponse assertPostResponse(final Object request, final String path, final int statusCode) {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post(path)
                .then().log().all()
                .statusCode(statusCode);
    }

    protected ValidatableResponse assertGetResponse(final String path, final int statusCode) {
        return RestAssured.given().log().all()
                .when().get(path)
                .then().log().all()
                .statusCode(statusCode);
    }

    protected ValidatableResponse assertGetResponseWithToken(final String token, final String path,
            final int statusCode
    ) {
        return RestAssured.given().log().all()
                .cookie("token", token)
                .when().get(path)
                .then().log().all()
                .statusCode(statusCode);
    }

    protected void assertDeleteResponse(final String path, final Long id, final int statusCode) {
        RestAssured.given().log().all()
                .when().delete(path + id)
                .then().log().all()
                .statusCode(statusCode);
    }
}
