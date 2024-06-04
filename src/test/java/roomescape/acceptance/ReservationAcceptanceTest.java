package roomescape.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import roomescape.fixture.ReservationFixture;
import roomescape.service.reservation.dto.ReservationRequest;

import java.time.LocalDate;
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
}
