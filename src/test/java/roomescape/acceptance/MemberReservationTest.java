package roomescape.acceptance;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

import io.restassured.RestAssured;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import roomescape.application.dto.response.payment.PaymentResponse;
import roomescape.domain.payment.PaymentClient;
import roomescape.fixture.CommonFixture;
import roomescape.support.DatabaseCleanerExtension;


@ExtendWith(DatabaseCleanerExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberReservationTest {
    private static final Map<String, String> TOKEN_CACHE = new HashMap<>();

    @MockBean
    private PaymentClient paymentClient;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        mockPaymentClient();
    }

    private void mockPaymentClient() {
        PaymentResponse responseBody = new PaymentResponse(
                10000L,
                "qwer",
                "1234abcd",
                "DONE",
                "2024-08-01T00:00:00",
                "2024-08-02T00:00:00");

        Mockito.when(paymentClient.confirmPayment(any()))
                .thenReturn(responseBody);
    }

    private String getToken(String email, String password) {
        if (TOKEN_CACHE.containsKey(email)) {
            return TOKEN_CACHE.get(email);
        }

        String requestBody = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);

        String token = RestAssured.given().log().all()
                .contentType("application/json")
                .body(requestBody)
                .when().post("/login")
                .then().log().all().statusCode(200)
                .extract().cookie("token");

        TOKEN_CACHE.put(email, token);
        return token;
    }

    @DisplayName("동일한 예약이 존재하지 않는 상황에, 예약 요청을 보내면, 예약된다")
    @Test
    @Sql(value = {"/test-data/members.sql", "/test-data/themes.sql", "/test-data/times.sql"})
    void when_noReservation_then_addReservation() {
        // given
        String requestBody = String.format(
                """
                        {
                            "themeId": %d,
                            "date": "%s",
                            "timeId": %d,
                            "orderId": "%s",
                            "paymentKey": "%s",
                            "paymentType": "%s"
                        }
                        """,
                1L, CommonFixture.tomorrow, 1L, CommonFixture.orderId, CommonFixture.paymentKey,
                CommonFixture.paymentType
        );

        // when, then
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201)
                .body("status", equalTo("RESERVED"));
    }

    @DisplayName("동일한 예약이 존재하는 상황에, 예약 요청을 보내면, 예약 대기 상태가 된다")
    @Test
    @Sql(value = {"/test-data/members.sql", "/test-data/themes.sql", "/test-data/times.sql"})
    void when_reservationExists_then_addWaitingReservation() {
        // given
        String requestBody = String.format(
                """
                        {
                            "themeId": %d,
                            "date": "%s",
                            "timeId": %d,
                            "orderId": "%s",
                            "paymentKey": "%s",
                            "paymentType": "%s"
                        }
                        """,
                1L, CommonFixture.tomorrow, 1L, CommonFixture.orderId, CommonFixture.paymentKey,
                CommonFixture.paymentType
        );

        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.adminEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201)
                .body("status", equalTo("RESERVED"));

        // when, then
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201)
                .body("status", equalTo("WAITING"));
    }

    @DisplayName("내가 예약한 상태에서, 예약 요청을 다시 보내면, 예약이 거절된다")
    @Test
    @Sql(value = {"/test-data/members.sql", "/test-data/themes.sql", "/test-data/times.sql"})
    void when_myReservationExists_then_rejectReservation() {
        // given
        String requestBody = String.format(
                """
                        {
                            "themeId": %d,
                            "date": "%s",
                            "timeId": %d,
                            "orderId": "%s",
                            "paymentKey": "%s",
                            "paymentType": "%s"
                        }
                        """,
                1L, CommonFixture.tomorrow, 1L, CommonFixture.orderId, CommonFixture.paymentKey,
                CommonFixture.paymentType
        );

        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201);

        // when, then
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(400);
    }

    @DisplayName("내가 예약 대기한 상태에서, 예약 요청을 보내면, 예약이 거절된다")
    @Test
    @Sql(value = {"/test-data/members.sql", "/test-data/themes.sql", "/test-data/times.sql"})
    void when_myWaitingReservationExists_then_rejectReservation() {
        // given
        String requestBody = String.format(
                """
                        {
                            "themeId": %d,
                            "date": "%s",
                            "timeId": %d,
                            "orderId": "%s",
                            "paymentKey": "%s",
                            "paymentType": "%s"
                        }
                        """,
                1L, CommonFixture.tomorrow, 1L, CommonFixture.orderId, CommonFixture.paymentKey,
                CommonFixture.paymentType
        );

        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.adminEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201);

        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201);

        // when, then
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(400);
    }

    @DisplayName("예약 대기를 취소한 상태에서, 예약 대기 요청을 보내면, 예약 대기된다")
    @Test
    @Sql(value = {"/test-data/members.sql", "/test-data/themes.sql", "/test-data/times.sql"})
    void when_canceledReservation_then_addReservation() {
        // given
        String requestBody = String.format(
                """
                        {
                            "themeId": %d,
                            "date": "%s",
                            "timeId": %d,
                            "orderId": "%s",
                            "paymentKey": "%s",
                            "paymentType": "%s"
                        }
                        """,
                1L, CommonFixture.tomorrow, 1L, CommonFixture.orderId, CommonFixture.paymentKey,
                CommonFixture.paymentType
        );

        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.adminEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201)
                .body("status", equalTo("RESERVED"));

        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201)
                .body("status", equalTo("WAITING"));

        // when
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().delete("/reservations/" + 2)
                .then().log().all()
                .assertThat()
                .statusCode(204);

        // then
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201)
                .body("status", equalTo("WAITING"));
    }

    @DisplayName("다른 사람의 예약 대기가 존재하는 상태에서, 내가 다른 사람의 예약 대기 취소 요청을 보내면, 요청은 거절된다")
    @Test
    @Sql(value = {"/test-data/members.sql", "/test-data/themes.sql", "/test-data/times.sql"})
    void when_anotherWaitingReservationExists_then_canNotDeleteOthersWaitingReservation() {
        // given
        String requestBody = String.format(
                """
                        {
                            "themeId": %d,
                            "date": "%s",
                            "timeId": %d,
                            "orderId": "%s",
                            "paymentKey": "%s",
                            "paymentType": "%s"
                        }
                        """,
                1L, CommonFixture.tomorrow, 1L, CommonFixture.orderId, CommonFixture.paymentKey,
                CommonFixture.paymentType
        );

        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.adminEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201);

        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userJazzEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201);

        // when, then
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .when().delete("/reservations/2")
                .then().log().all()
                .assertThat()
                .statusCode(403);
    }

    @DisplayName("뒤에 예약 대기가 존재하는 상태에서, 예약 대기를 취소하고 다시 예약 요청을 보내면, 예약 대기 상태가 된다")
    @Test
    @Sql(value = {"/test-data/members.sql", "/test-data/themes.sql", "/test-data/times.sql"})
    void when_canceledWaitingReservation_then_addWaitingReservation() {
        // given
        String requestBody = String.format(
                """
                        {
                            "themeId": %d,
                            "date": "%s",
                            "timeId": %d,
                            "orderId": "%s",
                            "paymentKey": "%s",
                            "paymentType": "%s"
                        }
                        """,
                1L, CommonFixture.tomorrow, 1L, CommonFixture.orderId, CommonFixture.paymentKey,
                CommonFixture.paymentType
        );

        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.adminEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201)
                .body("status", equalTo("RESERVED"));

        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201)
                .body("status", equalTo("WAITING"));

        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userJazzEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201)
                .body("status", equalTo("WAITING"));

        // when
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .when().delete("/reservations/" + 2)
                .then().log().all()
                .assertThat()
                .statusCode(204);

        // then
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201)
                .body("status", equalTo("WAITING"));
    }

    @DisplayName("이미 지난 시간에 대한 예약 요청을 보내면, 예약이 거절된다")
    @Test
    @Sql(value = {"/test-data/members.sql", "/test-data/themes.sql"})
    void when_pastTimeReservation_then_rejectReservation() {
        // given
        String requestBody = String.format(
                """
                        {
                            "themeId": %d,
                            "date": "%s",
                            "timeId": %d,
                            "orderId": "%s",
                            "paymentKey": "%s",
                            "paymentType": "%s"
                        }
                        """,
                1L, CommonFixture.yesterday, 1L, CommonFixture.orderId, CommonFixture.paymentKey,
                CommonFixture.paymentType
        );

        // when, then
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(400);
    }

    @DisplayName("존재하는 시간에 대한 예약 요청을 보내면, 예약된다")
    @Test
    @Sql(value = {"/test-data/members.sql", "/test-data/themes.sql", "/test-data/times.sql"})
    void when_reserveWithExistTime_then_rejectReservation() {
        // given
        String requestBody = String.format(
                """
                        {
                            "themeId": %d,
                            "date": "%s",
                            "timeId": %d,
                            "orderId": "%s",
                            "paymentKey": "%s",
                            "paymentType": "%s"
                        }
                        """,
                1L, CommonFixture.tomorrow, 1L, CommonFixture.orderId, CommonFixture.paymentKey,
                CommonFixture.paymentType
        );

        // when, then
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201);
    }

    @DisplayName("존재하지 않는 시간에 대한 예약 요청을 보내면, 예약이 거절된다")
    @Test
    @Sql(value = {"/test-data/members.sql", "/test-data/themes.sql", "/test-data/times.sql"})
    void when_noTimeReservation_then_rejectReservation() {
        // given
        String requestBody = String.format(
                """
                        {
                            "themeId": %d,
                            "date": "%s",
                            "timeId": %d,
                            "orderId": "%s",
                            "paymentKey": "%s",
                            "paymentType": "%s"
                        }
                        """,
                1L, CommonFixture.yesterday, 100L, CommonFixture.orderId, CommonFixture.paymentKey,
                CommonFixture.paymentType
        );

        // when, then
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(400);
    }

    @DisplayName("존재하지 않는 테마에 대한 예약 요청을 보내면, 예약이 거절된다")
    @Test
    @Sql(value = {"/test-data/members.sql", "/test-data/times.sql", "/test-data/themes.sql"})
    void when_noThemeReservation_then_rejectReservation() {
        // given
        String requestBody = String.format(
                """
                        {
                            "themeId": %d,
                            "date": "%s",
                            "timeId": %d,
                            "orderId": "%s",
                            "paymentKey": "%s",
                            "paymentType": "%s"
                        }
                        """,
                100L, CommonFixture.tomorrow, 1L, CommonFixture.orderId, CommonFixture.paymentKey,
                CommonFixture.paymentType
        );

        // when, then
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(400);
    }

    @DisplayName("예약이 존재하지 않는 상황에서, 예약을 취소 요청을 보내면, 요청을 무시한다")
    @Test
    @Sql(value = {"/test-data/members.sql", "/test-data/themes.sql", "/test-data/times.sql"})
    void when_noReservation_then_throwException() {
        // given
        long reservationId = 1L;

        // when, then
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .when().delete("/reservations/" + reservationId)
                .then().log().all()
                .assertThat()
                .statusCode(400);
    }

    @DisplayName("과거 예약에 대해 취소 요청을 보내면, 요청을 무시한다")
    @Test
    @Sql(value = {"/test-data/members.sql", "/test-data/themes.sql", "/test-data/times.sql"})
    void when_cancelPastTimeReservation_then_nothingHappens() {
        // when, then
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .when().delete("/reservations/1")
                .then().log().all()
                .assertThat()
                .statusCode(400);
    }

    @DisplayName("예약과 예약 대기가 모두 있는 상태에서, 모든 예약을 조회하면, 예약과 예약 대기를 모두 반환한다")
    @Test
    @Sql(value = {"/test-data/members.sql", "/test-data/themes.sql", "/test-data/times.sql",
            "/test-data/reservations-details.sql", "/test-data/reservations.sql"})
    void when_getReservations_then_returnReservations() {
        // when, then
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.adminEmail, CommonFixture.password))
                .when().get("/reservations-mine")
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .body("size()", is(11))
                .body("findAll { it.status == 'RESERVED' }.size()", is(8))
                .body("findAll { it.status == 'WAITING' }.size()", is(3));
    }

    @DisplayName("과거의 예약과 예약 대기는 조회되지 않는다")
    @Test
    @Sql(value = {"/test-data/members.sql", "/test-data/themes.sql", "/test-data/times.sql",
            "/test-data/reservations-details.sql", "/test-data/reservations.sql", "/test-data/past-reservations.sql"})
    void when_getReservations_then_doesNotReturnPastReservations() {
        // when, then
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.adminEmail, CommonFixture.password))
                .when().get("/reservations-mine")
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .body("size()", is(11))
                .body("findAll { it.status == 'RESERVED' }.size()", is(8))
                .body("findAll { it.status == 'WAITING' }.size()", is(3));
    }

    @DisplayName("내 예약 대기가 존재하는 경우에, 예약 대기 삭제 요청을 하면, 삭제된다")
    @Test
    @Sql(value = {"/test-data/members.sql", "/test-data/themes.sql", "/test-data/times.sql"})
    void when_myWaitingReservationExists_then_deleteWaitingReservation() {
        // when, then
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .when().delete("/reservations/" + 1)
                .then().log().all()
                .assertThat()
                .statusCode(400);
    }

    @DisplayName("내 예약 대기가 존재하지 않으면, 예약 대기를 삭제할 수 없다")
    @Test
    @Sql(value = {"/test-data/members.sql", "/test-data/themes.sql", "/test-data/times.sql"})
    void when_noWaitingReservation_then_canNotDeleteWaitingReservation() {
        // when, then
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .when().delete("/reservations/" + 1)
                .then().log().all()
                .assertThat()
                .statusCode(400);
    }

    @DisplayName("예약으로 전환된 상태에서, 예약 취소 요청하면, 정상적으로 취소된다")
    @Test
    @Sql(value = {"/test-data/members.sql", "/test-data/themes.sql", "/test-data/times.sql"})
    void when_reservationStatusChangedIntoResolved_then_canNotDeleteWaitingReservation() {
        // given
        String requestBody = String.format(
                """
                        {
                            "themeId": %d,
                            "date": "%s",
                            "timeId": %d,
                            "orderId": "%s",
                            "paymentKey": "%s",
                            "paymentType": "%s"
                        }
                        """,
                1L, CommonFixture.tomorrow, 1L, CommonFixture.orderId, CommonFixture.paymentKey,
                CommonFixture.paymentType
        );

        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.adminEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201);

        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201);

        // when, then
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.adminEmail, CommonFixture.password))
                .when().delete("/admin/reservations/" + 1)
                .then().log().all()
                .assertThat()
                .statusCode(204);

        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .when().delete("/reservations/" + 2)
                .then().log().all()
                .assertThat()
                .statusCode(204);
    }

    @DisplayName("예약 대기가 존재하는 상태에서, 예약 대기 취소 요청을 보내면, 정상적으로 취소된다")
    @Test
    @Sql(value = {"/test-data/members.sql", "/test-data/themes.sql", "/test-data/times.sql"})
    void when_waitingReservationExists_then_canNotDeleteResolvedReservation() {
        // given
        String requestBody = String.format(
                """
                        {
                            "themeId": %d,
                            "date": "%s",
                            "timeId": %d,
                            "orderId": "%s",
                            "paymentKey": "%s",
                            "paymentType": "%s"
                        }
                        """,
                1L, CommonFixture.tomorrow, 1L, CommonFixture.orderId, CommonFixture.paymentKey,
                CommonFixture.paymentType
        );

        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.adminEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201);

        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .body(requestBody).contentType("application/json")
                .when().post("/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(201);

        // when, then
        RestAssured.given().log().all()
                .cookie("token", getToken(CommonFixture.userMangEmail, CommonFixture.password))
                .when().delete("/reservations/2")
                .then().log().all().statusCode(204);
    }
}
