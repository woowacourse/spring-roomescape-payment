package roomescape.acceptance;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.test.context.jdbc.Sql;
import roomescape.service.auth.dto.LoginRequest;
import roomescape.service.reservation.dto.ReservationRequest;
import roomescape.service.waiting.dto.WaitingRequest;

@Sql("/truncate-with-time-and-theme.sql")
class ReservationAcceptanceTest extends AcceptanceTest {

    private LocalDate date;
    private long timeId;
    private long themeId;
    private String guest1Token;
    private String guest2Token;
    private String adminToken;

    @BeforeEach
    void init() {
        date = LocalDate.now().plusDays(1);
        timeId = 1;
        themeId = 1;

        adminToken = RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(new LoginRequest("admin123", "admin@email.com"))
            .when().post("/login")
            .then().log().all().extract().cookie("token");

        guest1Token = RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(new LoginRequest("guest123", "guest@email.com"))
            .when().post("/login")
            .then().log().all().extract().cookie("token");

        guest2Token = RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(new LoginRequest("guest123", "guest2@email.com"))
            .when().post("/login")
            .then().log().all().extract().cookie("token");
    }

    @DisplayName("예약 추가 성공 테스트")
    @Test
    void createReservation() {
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .cookie("token", guest1Token)
            .body(new ReservationRequest(date, timeId, themeId, "testPaymentKey", "testOrderId", 1000L))
            .when().post("/reservations")
            .then().log().all()
            .assertThat().statusCode(201).body("id", is(greaterThan(0)));
    }

    @DisplayName("예약 추가 실패 테스트 - 일정 오류")
    @Test
    void createInvalidScheduleReservation() {
        //given
        LocalDate invalidDate = LocalDate.now().minusDays(1);

        //when&then
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .cookie("token", guest1Token)
            .body(new ReservationRequest(invalidDate, timeId, themeId, "testPaymentKey", "testOrderId", 1000L))
            .when().post("/reservations")
            .then().log().all()
            .assertThat().statusCode(400).body("message", is("현재보다 이전으로 일정을 설정할 수 없습니다."));
    }

    @DisplayName("모든 예약 내역 조회 테스트")
    @TestFactory
    Stream<DynamicTest> findAllReservations() {
        return Stream.of(
            DynamicTest.dynamicTest("예약을 생성한다.", () -> {
                RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", guest1Token)
                    .body(new ReservationRequest(date, timeId, themeId, "testPaymentKey", "testOrderId", 1000L))
                    .when().post("/reservations");
            }),
            DynamicTest.dynamicTest("모든 예약 내역을 조회한다.", () -> {
                RestAssured.given().log().all()
                    .cookie("token", adminToken)
                    .when().get("/reservations")
                    .then().log().all()
                    .assertThat().statusCode(200).body("size()", is(1));
            })
        );
    }

    @DisplayName("이미 예약이 있는데, 또 예약 요청을 하면 예외를 발생시킨다.")
    @TestFactory
    Stream<DynamicTest> cannotCreateReservationBecauseAlreadyReserved() {
        return Stream.of(
            DynamicTest.dynamicTest("guest2이 예약을 생성한다.", () -> {
                RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", guest2Token)
                    .body(new ReservationRequest(date, timeId, themeId, "testPaymentKey", "testOrderId", 1000L))
                    .when().post("/reservations")
                    .then().log().all()
                    .assertThat().statusCode(201).body("status", is("예약"));
            }),
            DynamicTest.dynamicTest("guest1이 동일한 테마와 일정으로 다시 예약을 요청하면 예외가 발생한다.", () -> {
                RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", guest1Token)
                    .body(new ReservationRequest(date, timeId, themeId, "testPaymentKey", "testOrderId", 1000L))
                    .when().post("/reservations")
                    .then().log().all()
                    .assertThat().statusCode(400).body("message", is("이미 예약(대기)가 존재하여 예약이 불가능합니다."));
            })
        );
    }

    @DisplayName("이미 예약 대기가 있는데, 또 예약 요청을 하면 예외를 발생시킨다.")
    @TestFactory
    Stream<DynamicTest> cannotCreateReservationBecauseAlreadyWaiting() {
        return Stream.of(
            DynamicTest.dynamicTest("admin이 요청한 일정과 테마로 예약이 존재하지 않아서 예약 상태로 생성한다.", () -> {
                RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", adminToken)
                    .body(new ReservationRequest(date, timeId, themeId, "testPaymentKey", "testOrderId", 1000L))
                    .when().post("/reservations")
                    .then().log().all()
                    .assertThat().statusCode(201).body("status", is("예약"));
            }),
            DynamicTest.dynamicTest("guest2가 guest1과 동일한 테마와 일정으로 예약을 요청하고, 1번째 예약 대기 상태로 생성된다.", () -> {
                RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", guest2Token)
                    .body(new WaitingRequest(date, timeId, themeId))
                    .when().post("/waitings")
                    .then().log().all()
                    .assertThat().statusCode(201).body("status", is("예약대기"));
            }),
            DynamicTest.dynamicTest("guest1가 guest2와 동일한 테마와 일정으로 다시 예약을 요청하면 예외가 발생한다.", () -> {
                RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", guest1Token)
                    .body(new ReservationRequest(date, timeId, themeId, "testPaymentKey", "testOrderId", 1000L))
                    .when().post("/reservations")
                    .then().log().all()
                    .assertThat().statusCode(400).body("message", is("이미 예약(대기)가 존재하여 예약이 불가능합니다."));
            })
        );
    }
}
