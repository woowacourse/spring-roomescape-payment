package roomescape.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.fixture.ReservationDetailFixture;
import roomescape.fixture.ReservationFixture;
import roomescape.fixture.ScheduleFixture;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;

class MemberAcceptanceTest extends AcceptanceTest {
    @DisplayName("모든 사용자 정보를 조회한다.")
    @Test
    void findAllMembers() {
        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .when().get("/members")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value());
    }

    @DisplayName("본인의 모든 예약과 예약 대기를 조회한다.")
    @TestFactory
    Stream<DynamicTest> showAllOwnReservationsAndWaitings() {
        Theme theme = reservationDetail.getTheme();
        ReservationTime time = reservationDetail.getReservationTime();
        ReservationDetail anotherReservationDetail = ReservationDetailFixture.create(theme, ScheduleFixture.createFutureSchedule(14, time));

        return Stream.of(
                DynamicTest.dynamicTest("guest1이 새로운 예약을 추가한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", guestToken)
                            .body(ReservationFixture.createReservationRequest(reservationDetail))
                            .when().post("/reservations")
                            .then().log().all()
                            .assertThat().statusCode(201);
                }),
                DynamicTest.dynamicTest("guest1이 본인의 예약 내역을 조회하면 1개 내역이 조회된다.", () -> {
                    RestAssured.given().log().all()
                            .cookie("token", guestToken)
                            .when().get("/members/reservations")
                            .then().log().all()
                            .assertThat().statusCode(200)
                            .body("size()", is(1));
                }),
                DynamicTest.dynamicTest("admin이 새로운 예약을 추가한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", adminToken)
                            .body(ReservationFixture.createReservationRequest(anotherReservationDetail))
                            .when().post("/reservations")
                            .then().log().all()
                            .assertThat().statusCode(201).body("status", is(ReservationStatus.RESERVED.getDescription()));
                }),
                DynamicTest.dynamicTest("guest1이 admin과 동일한 테마와 일정으로 예약을 요청하고, 1번째 예약 대기 상태로 생성된다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", guestToken)
                            .body(ReservationFixture.createReservationRequest(anotherReservationDetail))
                            .when().post("/waitings")
                            .then().log().all()
                            .assertThat().statusCode(201).body("status", is(ReservationStatus.WAITING.getDescription()));
                }),
                DynamicTest.dynamicTest("guest1이 본인의 예약 내역을 조회하면 2개 내역이 조회된다.", () -> {
                    RestAssured.given().log().all()
                            .cookie("token", guestToken)
                            .when().get("/members/reservations")
                            .then().log().all()
                            .assertThat().statusCode(200).body("size()", is(2))
                            .body("[0].reservationStatus.status", is(ReservationStatus.RESERVED.getDescription()))
                            .body("[0].reservationStatus.rank", is(0))
                            .body("[1].reservationStatus.status", is(ReservationStatus.WAITING.getDescription()))
                            .body("[1].reservationStatus.rank", is(1));
                })
        );
    }
}
