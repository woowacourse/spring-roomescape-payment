package roomescape.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import roomescape.fixture.ReservationFixture;
import roomescape.fixture.WaitingFixture;

import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;

class WaitingAcceptanceTest extends AcceptanceTest {
    @DisplayName("모든 예약 대기 내역 조회할 수 있다.")
    @TestFactory
    Stream<DynamicTest> findAllWaitings() {
        return Stream.of(
                DynamicTest.dynamicTest("예약을 생성한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", guestToken)
                            .body(ReservationFixture.createReservationRequest(reservationDetail))
                            .when().post("/reservations")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.CREATED.value());
                }),
                DynamicTest.dynamicTest("admin이 guest1과 동일한 테마와 일정으로 예약을 요청하고, 1번째 예약 대기 상태로 생성된다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", adminToken)
                            .body(WaitingFixture.createWaitingRequest(reservationDetail))
                            .when().post("/waitings")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.CREATED.value());
                }),
                DynamicTest.dynamicTest("모든 예약 대기 내역을 조회한다.", () -> {
                    RestAssured.given().log().all()
                            .cookie("token", adminToken)
                            .when().get("/waitings")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.OK.value())
                            .body("size()", is(1));
                })
        );
    }

    @DisplayName("사용자는 본인의 것이 아닌 예약 대기를 삭제할 수 없다.")
    @TestFactory
    Stream<DynamicTest> cannotDeleteOtherWaiting() {
        AtomicLong waitingId = new AtomicLong();
        return Stream.of(
                DynamicTest.dynamicTest("예약을 생성한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", guestToken)
                            .body(ReservationFixture.createReservationRequest(reservationDetail))
                            .when().post("/reservations")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.CREATED.value());
                }),
                DynamicTest.dynamicTest("admin이 guest와 동일한 테마와 일정으로 예약을 요청하고, 1번째 예약 대기 상태로 생성된다.", () -> {
                    waitingId.set((int) RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", adminToken)
                            .body(WaitingFixture.createWaitingRequest(reservationDetail))
                            .when().post("/waitings")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.CREATED.value())
                            .extract().jsonPath().get("id"));
                }),
                DynamicTest.dynamicTest("guest가 admin의 예약 대기를 삭제하려고 하면 예외가 발생한다.", () -> {
                    RestAssured.given().log().all()
                            .cookie("token", guestToken)
                            .when().delete("/waitings/" + waitingId)
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.FORBIDDEN.value())
                            .body("message", is("예약 대기를 삭제할 권한이 없습니다."));
                })
        );
    }

    @DisplayName("사용자는 예약으로 전환된 예약 대기는 삭제할 수 없다.")
    @TestFactory
    Stream<DynamicTest> cannotDeleteByChangedToReserved() {
        AtomicLong reservationId = new AtomicLong();
        AtomicLong waitingId = new AtomicLong();
        return Stream.of(
                DynamicTest.dynamicTest("guest가 요청한 일정과 테마로 예약이 존재하지 않아서 예약 상태로 생성한다.", () -> {
                    reservationId.set((int) RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", guestToken)
                            .body(ReservationFixture.createReservationRequest(reservationDetail))
                            .when().post("/reservations")
                            .then().extract().body().jsonPath().get("id"));
                }),
                DynamicTest.dynamicTest("admin이 guest와 동일한 테마와 일정으로 예약을 요청하고, 1번째 예약 대기 상태로 생성된다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", adminToken)
                            .body(WaitingFixture.createWaitingRequest(reservationDetail))
                            .when().post("/waitings")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.CREATED.value()).body("status", is("예약대기"));
                }),
                DynamicTest.dynamicTest("admin이 guest의 예약을 삭제한다.", () -> {
                    RestAssured.given().log().all()
                            .cookie("token", adminToken)
                            .when().delete("/admin/reservations/" + reservationId)
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.NO_CONTENT.value());
                }),
                DynamicTest.dynamicTest("admin의 예약 대기가 예약으로 변경되었다.", () -> {
                    waitingId.set((int) RestAssured.given().log().all()
                            .cookie("token", adminToken)
                            .when().get("/members/reservations")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.OK.value())
                            .body("[0].reservationStatus.status", is("예약"))
                            .body("[0].reservationStatus.rank", is(0))
                            .extract().jsonPath().get("[0].reservationId"));
                }),
                DynamicTest.dynamicTest("admin은 예약 대기에서 예약으로 전환되었기 떄문에 본인의 예약 대기를 삭제할 수 없다.", () -> {
                    RestAssured.given().log().all()
                            .cookie("token", adminToken)
                            .when().delete("/waitings/" + waitingId)
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                            .body("message", is("예약은 삭제할 수 없습니다. 관리자에게 문의해주세요."));
                })
        );
    }

    @DisplayName("어드민은 예약으로 전환된 예약 대기는 삭제할 수 없다.")
    @TestFactory
    Stream<DynamicTest> cannotDeleteChangedToReservedByAdmin() {
        AtomicLong reservationId = new AtomicLong();
        AtomicLong waitingId = new AtomicLong();
        return Stream.of(
                DynamicTest.dynamicTest("guest가 요청한 일정과 테마로 예약이 존재하지 않아서 예약 상태로 생성한다.", () -> {
                    reservationId.set((int) RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", guestToken)
                            .body(ReservationFixture.createReservationRequest(reservationDetail))
                            .when().post("/reservations")
                            .then().extract().jsonPath().get("id"));
                }),
                DynamicTest.dynamicTest("admin이 guest와 동일한 테마와 일정으로 예약을 요청하고, 1번째 예약 대기 상태로 생성된다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", adminToken)
                            .body(WaitingFixture.createWaitingRequest(reservationDetail))
                            .when().post("/waitings")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.CREATED.value());
                }),
                DynamicTest.dynamicTest("admin이 guest의 예약을 삭제한다.", () -> {
                    RestAssured.given().log().all()
                            .cookie("token", adminToken)
                            .when().delete("/admin/reservations/" + reservationId)
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.NO_CONTENT.value());
                }),
                DynamicTest.dynamicTest("admin의 예약 대기가 예약으로 변경되었다.", () -> {
                    waitingId.set((int) RestAssured.given().log().all()
                            .cookie("token", adminToken)
                            .when().get("/members/reservations")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.OK.value())
                            .extract().jsonPath().get("[0].reservationId"));
                }),
                DynamicTest.dynamicTest("어드민이 예약 대기를 삭제하려고 했는데, 이미 예약으로 전환되어 삭제할 수 없다.", () -> {
                    RestAssured.given().log().all()
                            .cookie("token", adminToken)
                            .when().delete("/waitings/" + waitingId)
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                            .body("message", is("예약은 삭제할 수 없습니다. 관리자에게 문의해주세요."));
                })
        );
    }

    @DisplayName("예약이나 예약 대기가 존재한다면 예약 대기 요청을 할 수 없다.")
    @TestFactory
    Stream<DynamicTest> cannotCreateWaitingByAlreadyReserved() {
        return Stream.of(
                DynamicTest.dynamicTest("guest가 예약을 생성한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", guestToken)
                            .body(ReservationFixture.createReservationRequest(reservationDetail))
                            .when().post("/reservations")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.CREATED.value());
                }),
                DynamicTest.dynamicTest("guest가 동일한 테마와 일정으로 예약 대기를 요청하면, 예외가 발생한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", guestToken)
                            .body(WaitingFixture.createWaitingRequest(reservationDetail))
                            .when().post("/waitings")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                            .body("message", is("이미 예약(대기) 상태입니다."));
                })
        );
    }

    @DisplayName("예약이 없는데 예약 대기 요청을 할 수 없다.")
    @TestFactory
    Stream<DynamicTest> cannotCreateWaitingByNoReservation() {
        return Stream.of(
                DynamicTest.dynamicTest("guest가 예약이 없는 테마와 일정으로 예약 대기를 요청하면, 예외가 발생한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", guestToken)
                            .body(WaitingFixture.createWaitingRequest(reservationDetail))
                            .when().post("/waitings")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                            .body("message", is("존재하는 예약이 없습니다. 예약으로 다시 시도해주세요."));
                })
        );
    }
}
