package roomescape.acceptance;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.test.context.jdbc.Sql;
import roomescape.fixture.TokenFixture;
import roomescape.service.reservation.dto.ReservationRequest;
import roomescape.service.waiting.dto.WaitingRequest;

@Sql({"/truncate.sql", "/member.sql", "/theme.sql", "/time.sql", "/reservation-detail.sql"})
class WaitingAcceptanceTest extends AcceptanceTest {

    private final LocalDate date = LocalDate.now().plusDays(1);
    private final long timeId = 1;
    private final long themeId = 1;

    @DisplayName("모든 예약 대기 내역 조회 테스트")
    @TestFactory
    Stream<DynamicTest> findAllWaitings() {
        return Stream.of(
            DynamicTest.dynamicTest("예약을 생성한다.", () -> {
                RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", TokenFixture.getGuestLilyToken())
                    .body(new ReservationRequest(date, timeId, themeId, "testPaymentKey", "testOrderId", 1000L))
                    .when().post("/reservations");
            }),
            DynamicTest.dynamicTest("guest2가 guest1과 동일한 테마와 일정으로 예약을 요청하고, 1번째 예약 대기 상태로 생성된다.", () -> {
                RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", TokenFixture.getGuestTomiToken())
                    .body(new WaitingRequest(date, timeId, themeId))
                    .when().post("/waitings")
                    .then().log().all()
                    .assertThat().body("status", is("예약대기"));
            }),
            DynamicTest.dynamicTest("모든 예약 대기 내역을 조회한다.", () -> {
                RestAssured.given().log().all()
                    .cookie("token", TokenFixture.getGuestLilyToken())
                    .when().get("/waitings")
                    .then().log().all()
                    .assertThat().statusCode(200).body("size()", is(1));
            })
        );
    }

    @DisplayName("사용자는 본인의 것이 아닌 예약 대기를 삭제할 수 없다.")
    @TestFactory
    Stream<DynamicTest> cannotDeleteOtherWaiting() {
        AtomicLong reservationId = new AtomicLong();
        return Stream.of(
            DynamicTest.dynamicTest("guest1이 요청한 일정과 테마로 예약이 존재하지 않아서 예약 상태로 생성한다.", () -> {
                RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", TokenFixture.getGuestLilyToken())
                    .body(new ReservationRequest(date, timeId, themeId, "testPaymentKey", "testOrderId", 1000L))
                    .when().post("/reservations")
                    .then().log().all()
                    .assertThat().statusCode(201).body("status", is("예약"));
            }),
            DynamicTest.dynamicTest("guest2가 guest1과 동일한 테마와 일정으로 예약을 요청하고, 1번째 예약 대기 상태로 생성된다.", () -> {
                reservationId.set((int) RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", TokenFixture.getGuestTomiToken())
                    .body(new WaitingRequest(date, timeId, themeId))
                    .when().post("/waitings")
                    .then().extract().body().jsonPath().get("id"));
            }),
            DynamicTest.dynamicTest("guest1이 guest2의 예약 대기를 삭제하려고 하면 예외가 발생한다.", () -> {
                RestAssured.given().log().all()
                    .cookie("token", TokenFixture.getGuestLilyToken())
                    .when().delete("/waitings/" + reservationId)
                    .then().log().all()
                    .assertThat().statusCode(403).body("message", is("예약 대기를 삭제할 권한이 없습니다."));
            })
        );
    }

    @DisplayName("예약이나 예약 대기가 존재한다면 예약 대기 요청을 할 수 없다.")
    @TestFactory
    Stream<DynamicTest> cannotCreateWaitingByAlreadyReserved() {
        return Stream.of(
            DynamicTest.dynamicTest("예약을 생성한다.", () -> {
                RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", TokenFixture.getGuestLilyToken())
                    .body(new ReservationRequest(date, timeId, themeId, "testPaymentKey", "testOrderId", 1000L))
                    .when().post("/reservations");
            }),
            DynamicTest.dynamicTest("guest1 동일한 테마와 일정으로 예약 대기를 요청하면, 예외가 발생한다.", () -> {
                RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", TokenFixture.getGuestLilyToken())
                    .body(new WaitingRequest(date, timeId, themeId))
                    .when().post("/waitings")
                    .then().log().all()
                    .assertThat().body("message", is("이미 예약(대기) 상태입니다."));
            })
        );
    }

    @DisplayName("예약이 없는데 예약 대기 요청을 할 수 없다.")
    @TestFactory
    Stream<DynamicTest> cannotCreateWaitingByNoReservation() {
        return Stream.of(
            DynamicTest.dynamicTest("guest1 예약이 없는 테마와 일정으로 예약 대기를 요청하면, 예외가 발생한다.", () -> {
                RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", TokenFixture.getGuestTomiToken())
                    .body(new WaitingRequest(date, timeId, themeId))
                    .when().post("/waitings")
                    .then().log().all()
                    .assertThat().body("message", is("존재하는 예약이 없습니다. 예약으로 다시 시도해주세요."));
            })
        );
    }
}
