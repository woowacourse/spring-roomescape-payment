package roomescape.acceptance;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.test.context.jdbc.Sql;
import roomescape.fixture.TokenFixture;
import roomescape.service.reservation.dto.AdminReservationRequest;
import roomescape.service.reservation.dto.ReservationRequest;

@Sql({"/truncate.sql", "/member.sql", "/time.sql", "/theme.sql"})
class AdminReservationAcceptanceTest extends AcceptanceTest {

    private final LocalDate date = LocalDate.now().plusDays(1);
    private final long timeId = 1;
    private final long themeId = 1;

    @DisplayName("예약 추가 성공 테스트")
    @Test
    void createReservation() {
        long guestId = 2;

        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .cookie("token", TokenFixture.getAdminToken())
            .body(new AdminReservationRequest(date, guestId, timeId, themeId))
            .when().post("/admin/reservations")
            .then().log().all()
            .assertThat().statusCode(201).body("id", is(greaterThan(0)));
    }

    @DisplayName("조건별 예약 내역 조회 테스트 - 사용자, 테마")
    @Test
    @Sql({"/truncate.sql", "/member.sql", "/theme.sql", "/time.sql", "/reservation-detail.sql", "/reservation.sql"})
    void findByMemberAndTheme() {
        //when & then
        RestAssured.given().log().all()
            .cookie("token", TokenFixture.getAdminToken())
            .queryParam("memberId", 1)
            .queryParam("themeId", 2)
            .when().get("/admin/reservations/search")
            .then().log().all()
            .assertThat().statusCode(200).body("size()", is(0));
    }

    @DisplayName("조건별 예약 내역 조회 테스트 - 시작 날짜")
    @Test
    @Sql({"/truncate.sql", "/member.sql", "/theme.sql", "/time.sql", "/reservation-detail.sql", "/reservation.sql"})
    void findByDateFrom() {
        //when & then
        RestAssured.given().log().all()
            .cookie("token", TokenFixture.getAdminToken())
            .queryParam("dateFrom", LocalDate.now().plusDays(7).toString())
            .when().get("/admin/reservations/search")
            .then().log().all()
            .assertThat().statusCode(200).body("size()", is(3));
    }

    @DisplayName("조건별 예약 내역 조회 테스트 - 테마")
    @Test
    @Sql({"/truncate.sql", "/member.sql", "/theme.sql", "/time.sql", "/reservation-detail.sql", "/reservation.sql"})
    void findByTheme() {
        //when & then
        RestAssured.given().log().all()
            .cookie("token", TokenFixture.getAdminToken())
            .queryParam("themeId", 1)
            .when().get("/admin/reservations/search")
            .then().log().all()
            .assertThat().statusCode(200).body("size()", is(2));
    }

    @DisplayName("어드민이 예약을 취소한다.")
    @TestFactory
    Stream<DynamicTest> deleteReservationByAdmin() {
        AtomicLong reservationId = new AtomicLong();

        return Stream.of(
            DynamicTest.dynamicTest("예약을 저장하고, 식별자를 가져온다.", () -> {
                reservationId.set((int) RestAssured.given().contentType(ContentType.JSON)
                    .cookie("token", TokenFixture.getGuestTomiToken())
                    .body(new ReservationRequest(date, timeId, themeId, "testPaymentKey", "testOrderId", 1000L))
                    .when().post("/reservations")
                    .then().extract().body().jsonPath().get("id"));
            }),
            DynamicTest.dynamicTest("예약을 삭제한다.", () -> {
                RestAssured.given().log().all()
                    .cookie("token", TokenFixture.getAdminToken())
                    .when().delete("/admin/reservations/" + reservationId)
                    .then().log().all()
                    .assertThat().statusCode(204);
            }),
            DynamicTest.dynamicTest("남은 예약 개수는 총 0개이다.", () -> {
                RestAssured.given().log().all()
                    .cookie("token", TokenFixture.getAdminToken())
                    .when().get("/reservations")
                    .then().log().all()
                    .assertThat().body("size()", is(0));
            })
        );
    }

    @DisplayName("어드민은 이미 일정이 지난 예약을 삭제할 수 없다.")
    @TestFactory
    @Sql({"/truncate.sql", "/member.sql", "/theme.sql", "/time.sql", "/reservation-detail.sql", "/reservation.sql"})
    Stream<DynamicTest> cannotDeletePastReservation() {
        return Stream.of(
            DynamicTest.dynamicTest("관리자가 일정이 지난 예약을 삭제하려고 하면 예외가 발생한다.", () -> {
                long reservationId = 1;
                RestAssured.given().log().all()
                    .cookie("token", TokenFixture.getAdminToken())
                    .when().delete("/admin/reservations/" + reservationId)
                    .then().log().all()
                    .assertThat().statusCode(400).body("message", is("이미 지난 예약은 삭제할 수 없습니다."));
            })
        );
    }

    @DisplayName("사용자는 예약을 취소할 수 없다.")
    @TestFactory
    Stream<DynamicTest> cannotDeleteReservationByGuest() {
        AtomicLong reservationId = new AtomicLong();

        return Stream.of(
            DynamicTest.dynamicTest("예약을 저장하고, 식별자를 가져온다.", () -> {
                reservationId.set((int) RestAssured.given().contentType(ContentType.JSON)
                    .cookie("token", TokenFixture.getGuestLilyToken())
                    .body(new ReservationRequest(date, timeId, themeId, "testPaymentKey", "testOrderId", 1000L))
                    .when().post("/reservations")
                    .then().extract().body().jsonPath().get("id"));
            }),
            DynamicTest.dynamicTest("예약을 삭제한다.", () -> {
                RestAssured.given().log().all()
                    .cookie("token", TokenFixture.getGuestLilyToken())
                    .when().delete("/admin/reservations/" + reservationId)
                    .then().log().all()
                    .assertThat().statusCode(403)
                    .body("message", is("권한이 없습니다. 관리자에게 문의해주세요."));
            })
        );
    }
}
