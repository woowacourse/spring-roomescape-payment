package roomescape.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import static roomescape.fixture.MemberFixture.MEMBERS;
import static roomescape.fixture.MemberFixture.memberFixture;
import static roomescape.fixture.PaymentFixture.PAYMENTS;
import static roomescape.fixture.PaymentFixture.paymentFixture;
import static roomescape.fixture.ReservationFixture.RESERVATIONS;
import static roomescape.fixture.ReservationFixture.reservationFixture;
import static roomescape.fixture.TestFixture.AMOUNT;
import static roomescape.fixture.TestFixture.DATE_MAY_EIGHTH;
import static roomescape.fixture.TestFixture.ORDER_ID;
import static roomescape.fixture.TestFixture.PAYMENT_KEY;
import static roomescape.fixture.ThemeFixture.THEMES;
import static roomescape.fixture.ThemeFixture.themeFixture;
import static roomescape.fixture.TimeFixture.TIMES;
import static roomescape.fixture.TimeFixture.timeFixture;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.dto.reservation.AdminReservationSaveRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationWithPaymentRequest;

class ReservationAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("사용자가 예약을 성공적으로 생성하면 201을 응답한다.")
    void respondCreatedWhenCreateReservation() {
        final var member = saveMember(memberFixture(2L));
        final var time = saveTime(timeFixture(2L));
        final var theme = saveTheme(themeFixture(2L));
        final var request = new ReservationWithPaymentRequest(
                DATE_MAY_EIGHTH,
                time.getId(),
                theme.getId(),
                "paymentKey",
                "orderId",
                1000L
        );

        final var response = RestAssured.given().log().all()
                .cookie("token", accessToken(member.getId()))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .extract().as(ReservationResponse.class);

        assertAll(() -> {
            assertThat(response.name()).isEqualTo(member.getNameString());
            assertThat(response.date()).isEqualTo(DATE_MAY_EIGHTH);
            assertThat(response.time().id()).isEqualTo(time.getId());
            assertThat(response.theme().id()).isEqualTo(theme.getId());
        });
    }

    @Test
    @DisplayName("관리자가 예약을 성공적으로 생성하면 201을 응답한다.")
    void respondCreatedWhenAdminCreateReservation() {
        var admin = saveMember(memberFixture(1L));
        var time = saveTime(timeFixture(1L));
        var theme = saveTheme(themeFixture(1L));
        var reservation = reservationFixture(1L);
        var request = new AdminReservationSaveRequest(
                admin.getId(),
                reservation.getDate(),
                time.getId(),
                theme.getId()
        );

        final var response = RestAssured.given().log().all()
                .cookie("token", accessToken(admin.getId()))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201)
                .extract().as(ReservationResponse.class);

        assertAll(() -> {
            assertThat(response.name()).isEqualTo(admin.getNameString());
            assertThat(response.date()).isEqualTo(reservation.getDate());
            assertThat(response.time().id()).isEqualTo(time.getId());
            assertThat(response.theme().id()).isEqualTo(theme.getId());
        });
    }

    @Test
    @DisplayName("존재하지 않는 예약 시간으로 예약 생성 시 404를 응답한다.")
    void respondBadRequestWhenNotExistingReservationTime() {
        final var member = saveMember(memberFixture(2L));
        final var theme = saveTheme(themeFixture(1L));
        final var request = new ReservationWithPaymentRequest(
                DATE_MAY_EIGHTH,
                10L,
                theme.getId(),
                PAYMENT_KEY,
                ORDER_ID,
                AMOUNT
        );

        RestAssured.given().log().all()
                .cookie("token", accessToken(member.getId()))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(404);
    }

    @Test
    @DisplayName("존재하지 않는 테마로 예약 생성 시 404를 응답한다.")
    void respondBadRequestWhenNotExistingTheme() {
        final var member = saveMember(memberFixture(2L));
        final var time = saveTime(timeFixture(1L));
        final var request = new ReservationWithPaymentRequest(
                DATE_MAY_EIGHTH,
                time.getId(),
                100L,
                PAYMENT_KEY,
                ORDER_ID,
                AMOUNT
        );

        RestAssured.given().log().all()
                .cookie("token", accessToken(member.getId()))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(404);
    }

    @Test
    @DisplayName("예약 목록을 성공적으로 조회하면 200을 응답한다.")
    void respondOkWhenFindReservations() {
        MEMBERS.forEach(this::saveMember);
        TIMES.forEach(this::saveTime);
        THEMES.forEach(this::saveTheme);
        PAYMENTS.forEach(this::savePayment);
        RESERVATIONS.forEach(this::saveReservation);
        final var response = RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .extract().as(new TypeRef<List<ReservationResponse>>() {});

        assertThat(response).hasSize(4);
    }

    @Test
    @DisplayName("테마, 사용자, 예약 날짜로 예약 목록을 성공적으로 조회하면 200을 응답한다.")
    void respondOkWhenFilteredFindReservations() {
        final var admin = saveMember(memberFixture(1L));
        final var member = saveMember(memberFixture(2L));
        final var time = saveTime(timeFixture(1L));
        final var theme = saveTheme(themeFixture(1L));
        final var dateFrom = "2034-05-01";
        final var dateTo = "2034-05-08";
        saveReservation(Reservation.builder()
                .member(member)
                .date(LocalDate.parse(dateFrom))
                .time(time)
                .theme(theme)
                .status(ReservationStatus.RESERVED)
                .build()
        );

        final var queryParams = Map.of(
                "memberId", member.getId(),
                "themeId", theme.getId(),
                "dateFrom", dateFrom,
                "dateTo", dateTo
        );

        final var responses = RestAssured.given().log().all()
                .queryParams(queryParams)
                .cookie("token", accessToken(admin.getId()))
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .extract().as(new TypeRef<List<ReservationResponse>>() {});

        var response = responses.get(0);
        assertAll(
                () -> assertThat(response.id()).isEqualTo(1L),
                () -> assertThat(response.name()).isEqualTo(member.getNameString()),
                () -> assertThat(response.date()).isEqualTo(dateFrom),
                () -> assertThat(response.time().id()).isEqualTo(time.getId()),
                () -> assertThat(response.theme().id()).isEqualTo(theme.getId())
        );
    }

    @Test
    @DisplayName("예약을 성공적으로 삭제하면 204를 응답한다.")
    void respondNoContentWhenDeleteReservation() {
        saveTime(timeFixture(1L));
        saveMember(memberFixture(1L));
        saveTheme(themeFixture(1L));
        savePayment(paymentFixture(1L));
        var reservation = saveReservation(reservationFixture(1L));
        RestAssured.given().log().all()
                .when().delete("/reservations/" + reservation.getId())
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("존재하지 않는 예약을 삭제하면 404를 응답한다.")
    void respondBadRequestWhenDeleteNotExistingReservation() {
        RestAssured.given().log().all()
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(404);
    }

    @Test
    @DisplayName("특정 사용자의 예약 및 예약 대기 목록을 성공적으로 조회하면 200을 응답한다.")
    void respondOkWhenFindMyReservations() {
        var member = saveMember(memberFixture(2L));
        var time = saveTime(timeFixture(1L));
        var theme = saveTheme(themeFixture(1L));
        var payment = savePayment(paymentFixture(1L));
        saveReservation(Reservation.builder()
                .member(member)
                .date(DATE_MAY_EIGHTH)
                .time(time)
                .theme(theme)
                .payment(payment)
                .status(ReservationStatus.RESERVED)
                .build());

        RestAssured.given().log().all()
                .cookie("token", accessToken(member.getId()))
                .when().get("/reservations/mine")
                .then().log().all()
                .statusCode(200);
    }
}
