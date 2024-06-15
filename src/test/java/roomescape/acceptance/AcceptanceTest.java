package roomescape.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.StandardSoftAssertionsProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import roomescape.TestFixture;
import roomescape.auth.dto.request.LoginRequest;
import roomescape.common.DatabaseCleaner;
import roomescape.member.domain.Email;
import roomescape.member.domain.Member;
import roomescape.member.domain.Name;
import roomescape.member.dto.request.MemberJoinRequest;
import roomescape.member.dto.response.MemberResponse;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.dto.request.ReservationPayRequest;
import roomescape.reservation.dto.request.ReservationSaveRequest;
import roomescape.reservation.dto.request.ReservationTimeSaveRequest;
import roomescape.reservation.dto.request.ThemeSaveRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.ReservationTimeResponse;
import roomescape.reservation.dto.response.ThemeResponse;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.ADMIN_EMAIL;
import static roomescape.TestFixture.ADMIN_NAME;
import static roomescape.TestFixture.MIA_RESERVATION_TIME;
import static roomescape.TestFixture.TEST_PASSWORD;
import static roomescape.TestFixture.THEME_THUMBNAIL;
import static roomescape.TestFixture.WOOTECO_THEME_DESCRIPTION;
import static roomescape.TestFixture.WOOTECO_THEME_NAME;
import static roomescape.member.domain.Role.ADMIN;
import static roomescape.member.domain.Role.USER;

@ActiveProfiles(value = "test")
@Import(TestPaymentConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {
    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.clear();
    }

    protected Long createTestTheme() {
        ThemeSaveRequest request = new ThemeSaveRequest(WOOTECO_THEME_NAME, WOOTECO_THEME_DESCRIPTION, THEME_THUMBNAIL);
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/themes")
                .then().extract()
                .as(ThemeResponse.class)
                .id();
    }

    protected Long createTestReservationTime() {
        ReservationTimeSaveRequest request = new ReservationTimeSaveRequest(MIA_RESERVATION_TIME);
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/times")
                .then().extract()
                .as(ReservationTimeResponse.class)
                .id();
    }

    protected Member createTestMember(String email, String name) {
        MemberJoinRequest request = new MemberJoinRequest(email, TEST_PASSWORD, name);
        MemberResponse response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/members/join")
                .then().extract()
                .as(MemberResponse.class);
        return new Member(response.id(), new Name(response.name()), new Email(response.email()), request.password(), USER);
    }

    protected Long createTestReservation(LocalDate date, Long timeId, Long themeId, String token, ReservationStatus status) {
        ReservationSaveRequest reservationSaveRequest = new ReservationSaveRequest(date, timeId, themeId);
        Cookie cookie = new Cookie.Builder("token", token).build();
        if (status.isBooking()) {
            return createTestBooking(reservationSaveRequest, cookie);
        }
        return createTestWaiting(reservationSaveRequest, cookie);
    }

    protected Long createTestBooking(ReservationSaveRequest reservationSaveRequest, Cookie cookie) {
        ReservationPayRequest reservationPayRequest = new ReservationPayRequest(reservationSaveRequest, TestFixture.PRODUCT_PAY_REQUEST());
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .cookie(cookie)
                .body(reservationPayRequest)
                .when().post("/reservations")
                .then().extract().as(ReservationResponse.class)
                .id();
    }

    protected Long createTestWaiting(ReservationSaveRequest reservationSaveRequest, Cookie cookie) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .cookie(cookie)
                .body(reservationSaveRequest)
                .when().post("/reservations/waiting")
                .then().extract().as(ReservationResponse.class)
                .id();
    }

    protected Member createTestAdmin() {
        MemberJoinRequest request = new MemberJoinRequest(ADMIN_EMAIL, TEST_PASSWORD, ADMIN_NAME);
        MemberResponse response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/members/join/admin")
                .then().extract()
                .as(MemberResponse.class);
        return new Member(response.id(), new Name(response.name()), new Email(response.email()), request.password(), ADMIN);
    }

    protected String createTestToken(String email) {
        LoginRequest request = new LoginRequest(email, TEST_PASSWORD);
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/login")
                .getCookie("token");
    }

    protected void checkHttpStatusOk(
            StandardSoftAssertionsProvider softAssertionsProvider, ExtractableResponse<Response> response) {
        softAssertionsProvider.assertThat(response.statusCode())
                .isEqualTo(HttpStatus.OK.value());
    }

    protected void checkHttpStatusCreated(
            StandardSoftAssertionsProvider softAssertionsProvider, ExtractableResponse<Response> response) {
        softAssertionsProvider.assertThat(response.statusCode())
                .isEqualTo(HttpStatus.CREATED.value());
    }

    protected void checkHttpStatusNoContent(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    protected void checkHttpStatusBadRequest(
            StandardSoftAssertionsProvider softAssertionsProvider, ExtractableResponse<Response> response) {
        softAssertionsProvider.assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    protected void checkHttpStatusNotFound(
            StandardSoftAssertionsProvider softAssertionsProvider, ExtractableResponse<Response> response) {
        softAssertionsProvider.assertThat(response.statusCode())
                .isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    protected void checkHttpStatusUnauthorized(
            StandardSoftAssertionsProvider softAssertionsProvider, ExtractableResponse<Response> response) {
        softAssertionsProvider.assertThat(response.statusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
