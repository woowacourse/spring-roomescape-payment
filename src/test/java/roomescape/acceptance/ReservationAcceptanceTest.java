package roomescape.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.fixture.PaymentFixture;
import roomescape.fixture.ReservationFixture;
import roomescape.fixture.WaitingFixture;
import roomescape.service.reservation.dto.ReservationRequest;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;

class ReservationAcceptanceTest extends AcceptanceTest {
    @DisplayName("새로운 예약을 추가한다.")
    @Test
    void createReservation() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", guestToken)
                .body(ReservationFixture.createReservationRequest(reservationDetail))
                .when().post("/reservations")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("잘못된 값으로 요청하면 예약이 생성되지 않는다.")
    @Test
    void createInvalidScheduleReservation() {
        //given
        LocalDate invalidDate = LocalDate.now().minusDays(1);

        //when&then
        ReservationRequest request = ReservationFixture.createReservationRequest(invalidDate, reservationDetail.getReservationTime().getId(), reservationDetail.getTheme());
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", guestToken)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", is("현재보다 이전으로 일정을 설정할 수 없습니다."));
    }

    @DisplayName("모든 예약 내역 조회 테스트")
    @Test
    void findAllReservations() {
        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .when().get("/reservations")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value());
    }

    @DisplayName("이미 예약이 있는데, 또 예약 요청을 하면 예외를 발생시킨다.")
    @TestFactory
    Stream<DynamicTest> cannotCreateReservationBecauseAlreadyReserved() {
        return Stream.of(
                DynamicTest.dynamicTest("admin이 예약을 생성한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", adminToken)
                            .body(ReservationFixture.createReservationRequest(reservationDetail))
                            .when().post("/reservations")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.CREATED.value());
                }),
                DynamicTest.dynamicTest("guest가 동일한 테마와 일정으로 다시 예약을 요청하면 예외가 발생한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", guestToken)
                            .body(ReservationFixture.createReservationRequest(reservationDetail))
                            .when().post("/reservations")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                            .body("message", is("이미 예약(대기)가 존재하여 예약이 불가능합니다."));
                })
        );
    }

    @DisplayName("이미 예약 대기가 있는데, 또 예약 요청을 하면 예외를 발생시킨다.")
    @TestFactory
    Stream<DynamicTest> cannotCreateReservationBecauseAlreadyWaiting() {
        return Stream.of(
                DynamicTest.dynamicTest("guest가 요청한 일정과 테마로 예약이 존재하지 않아서 예약 상태로 생성한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", guestToken)
                            .body(ReservationFixture.createReservationRequest(reservationDetail))
                            .when().post("/reservations")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.CREATED.value());
                }),
                DynamicTest.dynamicTest("guest가 guest2와 동일한 테마와 일정으로 다시 예약을 요청하면 예외가 발생한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", guestToken)
                            .body(ReservationFixture.createReservationRequest(reservationDetail))
                            .when().post("/reservations")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                            .body("message", is("이미 예약(대기)가 존재하여 예약이 불가능합니다."));
                })
        );
    }

    @DisplayName("결제 대기로 전환된 예약 대기 내역에 대해 결제를 진행하면 예약 상태로 변경된다.")
    @TestFactory
    Stream<DynamicTest> payForPendingPayment() {
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
                    waitingId.set((int) RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", adminToken)
                            .body(WaitingFixture.createWaitingRequest(reservationDetail))
                            .when().post("/waitings")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.CREATED.value()).body("status", is(ReservationStatus.WAITING.getDescription()))
                            .extract().jsonPath().get("id"));
                }),
                DynamicTest.dynamicTest("admin이 guest의 예약을 삭제한다.", () -> {
                    RestAssured.given().log().all()
                            .cookie("token", adminToken)
                            .when().delete("/admin/reservations/" + reservationId)
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.NO_CONTENT.value());
                }),
                DynamicTest.dynamicTest("admin은 예약 대기에서 결제 대기로 전환되었다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", adminToken)
                            .when().get("/members/reservations")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.OK.value()).body("[0].reservationStatus.status", is(ReservationStatus.PENDING_PAYMENT.getDescription()));
                }),
                DynamicTest.dynamicTest("admin은 해당 결제 대기에 대해 결제를 진행해서 예약으로 전환된다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", adminToken)
                            .body(PaymentFixture.createPaymentRequest())
                            .when().post("/reservations/" + waitingId + "/payment")
                            .then().log().all()
                            .assertThat().statusCode(200).body("status", is(ReservationStatus.RESERVED.getDescription()));
                })
        );
    }

    @Disabled
    @DisplayName("예약 대기 상태인 예약에 대해 결제를 요청할 수 없다.")
    @TestFactory
    Stream<DynamicTest> cannotPayForWaiting() {
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
                    waitingId.set((int) RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", adminToken)
                            .body(WaitingFixture.createWaitingRequest(reservationDetail))
                            .when().post("/waitings")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.CREATED.value()).body("status", is(ReservationStatus.WAITING.getDescription()))
                            .extract().jsonPath().get("id"));
                }),
                DynamicTest.dynamicTest("admin은 해당 예약 대기에 대해 결제를 진행하려고 하면 예외가 발생한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", adminToken)
                            .when().post("/reservations/" + waitingId + "/payment")
                            .then().log().all()
                            .assertThat().statusCode(200).body("message", is("결재 대기 상태에서만 결재 가능합니다."));
                })
        );
    }

    @Disabled
    @DisplayName("예약 상태인 예약에 대해 결제를 요청할 수 없다.")
    @TestFactory
    Stream<DynamicTest> cannotPayForReserved() {
        AtomicLong reservationId = new AtomicLong();

        return Stream.of(
                DynamicTest.dynamicTest("guest가 요청한 일정과 테마로 예약이 존재하지 않아서 예약 상태로 생성한다.", () -> {
                    reservationId.set((int) RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", guestToken)
                            .body(ReservationFixture.createReservationRequest(reservationDetail))
                            .when().post("/reservations")
                            .then().extract().body().jsonPath().get("id"));
                }),
                DynamicTest.dynamicTest("guest는 예약에 대해 결제를 진행하려고 하면 예외가 발생한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", guestToken)
                            .when().post("/reservations/" + reservationId + "/payment")
                            .then().log().all()
                            .assertThat().statusCode(200).body("message", is("결재 대기 상태에서만 결재 가능합니다."));
                })
        );
    }

    @Disabled
    @DisplayName("본인이 예약한 결제 대기 예약이 아닌 경우, 결제를 할 수 없다.")
    @TestFactory
    Stream<DynamicTest> payForOtherPendingPayment() {
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
                    waitingId.set((int) RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", adminToken)
                            .body(WaitingFixture.createWaitingRequest(reservationDetail))
                            .when().post("/waitings")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.CREATED.value()).body("status", is(ReservationStatus.WAITING.getDescription()))
                            .extract().jsonPath().get("id"));
                }),
                DynamicTest.dynamicTest("admin이 guest의 예약을 삭제한다.", () -> {
                    RestAssured.given().log().all()
                            .cookie("token", adminToken)
                            .when().delete("/admin/reservations/" + reservationId)
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.NO_CONTENT.value());
                }),
                DynamicTest.dynamicTest("admin은 예약 대기에서 결제 대기로 전환되었다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", adminToken)
                            .when().get("/members/reservations")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.OK.value()).body("[0].reservationStatus.status", is(ReservationStatus.PENDING_PAYMENT.getDescription()));
                }),
                DynamicTest.dynamicTest("guest가 해당 결제 대기에 대해 결제를 진행하려고 하면 예외가 발생한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", guestToken)
                            .when().post("/reservations/" + waitingId + "/payment")
                            .then().log().all()
                            .assertThat().statusCode(200).body("message", is("본인의 예약만 결제할 수 있습니다."));
                })
        );
    }

    @DisplayName("존재하지 않는 결제 대기에 대해 결제 요청을 할 수 없다.")
    @TestFactory
    Stream<DynamicTest> cannotPayForUnknownReservation() {
        AtomicLong reservationId = new AtomicLong();
        AtomicLong waitingId = new AtomicLong();

        return Stream.of(
                DynamicTest.dynamicTest("admin이 요청한 일정과 테마로 예약이 존재하지 않아서 예약 상태로 생성한다.", () -> {
                    reservationId.set((int) RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", adminToken)
                            .body(ReservationFixture.createReservationRequest(reservationDetail))
                            .when().post("/reservations")
                            .then().extract().body().jsonPath().get("id"));
                }),
                DynamicTest.dynamicTest("guest가 admin과 동일한 테마와 일정으로 예약을 요청하고, 1번째 예약 대기 상태로 생성된다.", () -> {
                    waitingId.set((int) RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", guestToken)
                            .body(WaitingFixture.createWaitingRequest(reservationDetail))
                            .when().post("/waitings")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.CREATED.value()).body("status", is(ReservationStatus.WAITING.getDescription()))
                            .extract().jsonPath().get("id"));
                }),
                DynamicTest.dynamicTest("admin이 admin의 예약을 삭제한다.", () -> {
                    RestAssured.given().log().all()
                            .cookie("token", adminToken)
                            .when().delete("/admin/reservations/" + reservationId)
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.NO_CONTENT.value());
                }),
                DynamicTest.dynamicTest("guest는 예약 대기에서 결제 대기로 전환되었다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", guestToken)
                            .when().get("/members/reservations")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.OK.value()).body("[0].reservationStatus.status", is(ReservationStatus.PENDING_PAYMENT.getDescription()));
                }),
                DynamicTest.dynamicTest("admin은 guest의 결제 대기 상태인 예약 정보를 삭제한다.", () -> {
                    RestAssured.given().log().all()
                            .cookie("token", adminToken)
                            .when().delete("/waitings/" + waitingId)
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.NO_CONTENT.value());
                }),
                DynamicTest.dynamicTest("admin은 방금 취소한 결제 대기에 대해 결제를 진행하려고 하면 예외가 발생한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", adminToken)
                            .body(PaymentFixture.createPaymentRequest())
                            .when().post("/reservations/" + waitingId + "/payment")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.BAD_REQUEST.value()).body("message", is("더이상 존재하지 않는 결제 대기 정보입니다."));
                })
        );
    }
}
