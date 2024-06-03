package roomescape.acceptance;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static roomescape.fixture.TokenFixture.getAdminToken;
import static roomescape.fixture.TokenFixture.getGuestLilyToken;
import static roomescape.fixture.TokenFixture.getGuestTomiToken;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.test.context.jdbc.Sql;
import roomescape.service.reservation.dto.ReservationRequest;
import roomescape.service.waiting.dto.WaitingRequest;

@Sql({"/truncate.sql", "/member.sql", "/time.sql", "/theme.sql"})
class ReservationAcceptanceTest extends AcceptanceTest {

    private final LocalDate date = LocalDate.now().plusDays(1);
    ;
    private final long timeId = 1;
    private final long themeId = 1;

    @DisplayName("예약 추가 성공 테스트")
    @Test
    void createReservation() {
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .cookie("token", getGuestLilyToken())
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
            .cookie("token", getGuestLilyToken())
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
                    .cookie("token", getGuestLilyToken())
                    .body(new ReservationRequest(date, timeId, themeId, "testPaymentKey", "testOrderId", 1000L))
                    .when().post("/reservations");
            }),
            DynamicTest.dynamicTest("모든 예약 내역을 조회한다.", () -> {
                RestAssured.given().log().all()
                    .cookie("token", getAdminToken())
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
                    .cookie("token", getGuestTomiToken())
                    .body(new ReservationRequest(date, timeId, themeId, "testPaymentKey", "testOrderId", 1000L))
                    .when().post("/reservations")
                    .then().log().all()
                    .assertThat().statusCode(201).body("status", is("예약"));
            }),
            DynamicTest.dynamicTest("guest1이 동일한 테마와 일정으로 다시 예약을 요청하면 예외가 발생한다.", () -> {
                RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", getGuestLilyToken())
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
                    .cookie("token", getAdminToken())
                    .body(new ReservationRequest(date, timeId, themeId, "testPaymentKey", "testOrderId", 1000L))
                    .when().post("/reservations")
                    .then().log().all()
                    .assertThat().statusCode(201).body("status", is("예약"));
            }),
            DynamicTest.dynamicTest("guest2가 guest1과 동일한 테마와 일정으로 예약을 요청하고, 1번째 예약 대기 상태로 생성된다.", () -> {
                RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", getGuestTomiToken())
                    .body(new WaitingRequest(date, timeId, themeId))
                    .when().post("/waitings")
                    .then().log().all()
                    .assertThat().statusCode(201).body("status", is("예약대기"));
            }),
            DynamicTest.dynamicTest("guest1가 guest2와 동일한 테마와 일정으로 다시 예약을 요청하면 예외가 발생한다.", () -> {
                RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", getGuestLilyToken())
                    .body(new ReservationRequest(date, timeId, themeId, "testPaymentKey", "testOrderId", 1000L))
                    .when().post("/reservations")
                    .then().log().all()
                    .assertThat().statusCode(400).body("message", is("이미 예약(대기)가 존재하여 예약이 불가능합니다."));
            })
        );
    }
}
