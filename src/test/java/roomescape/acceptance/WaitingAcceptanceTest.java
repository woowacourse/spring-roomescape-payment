package roomescape.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import static roomescape.fixture.MemberFixture.MEMBERS;
import static roomescape.fixture.MemberFixture.memberFixture;
import static roomescape.fixture.PaymentFixture.PAYMENTS;
import static roomescape.fixture.PaymentFixture.paymentFixture;
import static roomescape.fixture.ReservationFixture.RESERVATIONS;
import static roomescape.fixture.ReservationFixture.WAITINGS;
import static roomescape.fixture.ReservationFixture.reservationFixture;
import static roomescape.fixture.ThemeFixture.THEMES;
import static roomescape.fixture.ThemeFixture.themeFixture;
import static roomescape.fixture.TimeFixture.TIMES;
import static roomescape.fixture.TimeFixture.timeFixture;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationWithPaymentRequest;

public class WaitingAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("예약 대기를 성공적으로 등록하면 201을 응답한다.")
    void respondOkWhenCreateReservationWaiting() {
        saveMember(memberFixture(1L));
        final var member2 = saveMember(memberFixture(2L));
        final var time = saveTime(timeFixture(1L));
        final var theme = saveTheme(themeFixture(1L));
        savePayment(paymentFixture(1L));
        final var reservation = saveReservation(reservationFixture(1L));
        final var request = new ReservationWithPaymentRequest(
                reservation.getDate(),
                time.getId(),
                theme.getId(),
                null,
                null,
                null
        );

        final var response = RestAssured.given().log().all()
                .cookie("token", accessToken(member2.getId()))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(201)
                .extract().as(ReservationResponse.class);

        assertAll(() -> {
            assertThat(response.name()).isEqualTo(member2.getNameString());
            assertThat(response.date()).isEqualTo(reservation.getDate());
            assertThat(response.time().id()).isEqualTo(time.getId());
            assertThat(response.theme().id()).isEqualTo(theme.getId());
        });
    }

    @Test
    @DisplayName("예약 대기 목록을 성공적으로 조회하면 200을 응답한다.")
    void respondOkWhenFindReservationWaitings() {
        MEMBERS.forEach(this::saveMember);
        TIMES.forEach(this::saveTime);
        THEMES.forEach(this::saveTheme);
        PAYMENTS.forEach(this::savePayment);
        RESERVATIONS.forEach(this::saveReservation);
        WAITINGS.forEach(this::saveWaiting);

        final var admin = saveMember(memberFixture(1L));

        var responses = RestAssured.given().log().all()
                .cookie("token", accessToken(admin.getId()))
                .when().get("/admin/waitings")
                .then().log().all()
                .statusCode(200)
                .extract().as(new TypeRef<List<ReservationResponse>>() {});

        assertThat(responses.size()).isEqualTo(WAITINGS.size());
    }

    @Test
    @DisplayName("예약 대기를 성공적으로 승인하면 200을 응답한다.")
    void respondOkWhenApproveReservationWaiting() {
        MEMBERS.forEach(this::saveMember);
        TIMES.forEach(this::saveTime);
        THEMES.forEach(this::saveTheme);
        PAYMENTS.forEach(this::savePayment);
        RESERVATIONS.forEach(this::saveReservation);
        Member admin = memberFixture(1L);
        var reservation = reservationFixture(2L);
        Reservation waiting = saveWaiting(Reservation.builder()
                .member(reservation.getMember())
                .date(reservation.getDate())
                .time(reservation.getTime())
                .theme(reservation.getTheme())
                .build()
        );

        RestAssured.given().log().all()
                .when().delete("/reservations/2")
                .then().log().all()
                .statusCode(204);

        RestAssured.given().log().all()
                .cookie("token", accessToken(admin.getId()))
                .when().patch("/admin/waitings/" + waiting.getId() + "/approve")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("예약 대기를 성공적으로 거절하면 204를 응답한다.")
    void responseNoContentWhenRejectReservationWaiting() {
        MEMBERS.forEach(this::saveMember);
        TIMES.forEach(this::saveTime);
        THEMES.forEach(this::saveTheme);
        PAYMENTS.forEach(this::savePayment);
        RESERVATIONS.forEach(this::saveReservation);
        Member admin = memberFixture(1L);
        var reservation = reservationFixture(2L);
        Reservation waiting = saveWaiting(Reservation.builder()
                .member(reservation.getMember())
                .date(reservation.getDate())
                .time(reservation.getTime())
                .theme(reservation.getTheme())
                .build()
        );

        RestAssured.given().log().all()
                .when().delete("/reservations/2")
                .then().log().all()
                .statusCode(204);

        RestAssured.given().log().all()
                .cookie("token", accessToken(admin.getId()))
                .when().delete("/admin/waitings/" + waiting.getId())
                .then().log().all()
                .statusCode(204);
    }
}
