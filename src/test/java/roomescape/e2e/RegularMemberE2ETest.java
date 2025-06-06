package roomescape.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static roomescape.fixture.IntegrationFixture.ADMIN_EMAIL;
import static roomescape.fixture.IntegrationFixture.FUTURE_DATE_TEXT;
import static roomescape.fixture.IntegrationFixture.PASSWORD;
import static roomescape.fixture.IntegrationFixture.REGULAR2_EMAIL;
import static roomescape.fixture.IntegrationFixture.REGULAR_EMAIL;
import static roomescape.fixture.IntegrationFixture.TOKEN;
import static roomescape.fixture.IntegrationFixture.createRegularReservation;
import static roomescape.fixture.IntegrationFixture.createReservationTime;
import static roomescape.fixture.IntegrationFixture.createTheme;
import static roomescape.fixture.IntegrationFixture.loginAndGetAuthToken;
import static roomescape.fixture.IntegrationFixture.makeWaitingReservations;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.common.security.dto.request.LoginRequest;
import roomescape.common.security.dto.response.CheckLoginResponse;
import roomescape.payment.application.client.PaymentClient;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.payment.presentation.dto.response.PaymentApproveResponse;
import roomescape.reservationslot.presentation.dto.response.MyReservationResponse;
import roomescape.reservationslot.presentation.dto.response.ReservationResponse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.sql.init.data-locations=classpath:test-data.sql"
})
class RegularMemberE2ETest {

    @MockitoBean
    private PaymentClient paymentClient;

    @LocalServerPort
    private int port;

    private String REGULAR_TOKEN;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        REGULAR_TOKEN = loginAndGetAuthToken(REGULAR_EMAIL, PASSWORD);
        when(paymentClient.approvePayment(any())).thenAnswer(invocation -> {
            PaymentApproveRequest req = invocation.getArgument(0);
            return new PaymentApproveResponse(req.paymentKey(), req.orderId(), req.amount());
        });
    }

    @Test
    void 로그아웃을_할_수_있다() {
        RestAssured.given().log().all()
                .body(new LoginRequest(REGULAR_EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookie(TOKEN, REGULAR_TOKEN)
                .when().post("/logout")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    void 로그인_상태를_확인할_수_있다() {
        CheckLoginResponse checkLoginResponse = RestAssured.given().log().all()
                .body(new LoginRequest(REGULAR_EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookie(TOKEN, REGULAR_TOKEN)
                .when().get("/login/check")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(CheckLoginResponse.class);

        assertThat(checkLoginResponse.name()).isEqualTo("Regular");
    }


    @Test
    void 예약을_생성할_수_있다() {
        createReservationTime();
        createTheme("추리");
        createRegularReservation(1L, "testtest", "orderorder", 1000L);
        String adminToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);

        RestAssured.given().log().all()
                .cookie(TOKEN, adminToken)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void 로그인하지_않고_관리자_페이지에_접근할_경우_Unauthorize_예외가_발생한다() {
        RestAssured.given().log().all()
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    void 로그인하고_관리자_페이지에_접근할_경우_Forbidden_예외가_발생한다() {
        RestAssured.given().log().all()
                .cookie(TOKEN, REGULAR_TOKEN)
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(403);
    }

    @Test
    void 예약_대기를_생성할_수_있다() {
        ReservationResponse reservationResponse = makeWaitingReservations();

        assertThat(reservationResponse).isNotNull();
    }

    @Test
    void 내_예약을_조회할_수_있다() {
        예약을_생성할_수_있다();
        String user2Token = loginAndGetAuthToken(REGULAR_EMAIL, PASSWORD);

        List<MyReservationResponse> responses = RestAssured.given().log().all()
                .cookie(TOKEN, user2Token)
                .when().get("/reservations-mine")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(responses.size()).isEqualTo(1);
            softAssertions.assertThat(responses.getFirst().isReserved()).isTrue();
            softAssertions.assertThat(responses.getFirst().paymentKey()).isEqualTo("testtest");
            softAssertions.assertThat(responses.getFirst().amount()).isEqualTo(1000L);
        });
    }

    @Test
    void 대기_중인_예약을_삭제할_수_있다() {
        createReservationTime();
        createTheme("추리");
        createRegularReservation(1L, "testtest", "orderorder", 10000L);

        String user2Token = loginAndGetAuthToken(REGULAR2_EMAIL, PASSWORD);
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", FUTURE_DATE_TEXT);
        reservation.put("timeId", 1L);
        reservation.put("themeId", 1L);

        ReservationResponse reservationResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(TOKEN, user2Token)
                .body(reservation)
                .when().post("/waiting-reservations")
                .then().log().all()
                .statusCode(201)
                .extract()
                .as(ReservationResponse.class);

        Long reservationSlotId = reservationResponse.reservationSlotId();

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(TOKEN, user2Token)
                .pathParam("reservationSlotId", reservationSlotId)
                .when().delete("/waiting-reservations/{reservationSlotId}")
                .then().log().all()
                .statusCode(204);

        List<MyReservationResponse> responses = RestAssured.given().log().all()
                .cookie(TOKEN, user2Token)
                .when().get("/reservations-mine")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });
        assertThat(responses).isEmpty();
    }
}
