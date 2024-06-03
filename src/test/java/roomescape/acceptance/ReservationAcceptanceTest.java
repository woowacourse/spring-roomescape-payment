package roomescape.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.application.member.dto.request.MemberRegisterRequest;
import roomescape.application.member.dto.response.MemberResponse;
import roomescape.application.reservation.dto.request.ReservationPaymentRequest;
import roomescape.application.reservation.dto.request.ReservationRequest;
import roomescape.application.reservation.dto.request.ThemeRequest;
import roomescape.application.reservation.dto.response.ReservationResponse;
import roomescape.application.reservation.dto.response.ReservationStatusResponse;
import roomescape.application.reservation.dto.response.ReservationTimeResponse;
import roomescape.application.reservation.dto.response.ThemeResponse;

class ReservationAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("관리자가 예약을 삭제한다.")
    void deleteReservation() {
        long themeId = fixture.createTheme(
                new ThemeRequest("name", "desc", "url", 10_000L)
        ).id();
        long timeId = fixture.createReservationTime(10, 0).id();
        fixture.registerMember(new MemberRegisterRequest("name", "email@mail.com", "12341234"));
        String token = fixture.loginAndGetToken("email@mail.com", "12341234");
        ReservationResponse response = fixture.createReservation(
                token,
                new ReservationRequest(LocalDate.of(2024, 12, 25), timeId, themeId)
        );

        RestAssured.given().log().all()
                .cookie("token", token)
                .when().delete("/reservations/{id}", response.id())
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("나의 예약 목록을 조회한다.")
    void findMyReservations() {
        long themeId = fixture.createTheme(
                new ThemeRequest("name", "desc", "url", 10_000L)
        ).id();
        long timeId = fixture.createReservationTime(10, 0).id();
        fixture.registerMember(new MemberRegisterRequest("name", "email@mail.com", "12341234"));
        String token = fixture.loginAndGetToken("email@mail.com", "12341234");
        fixture.createReservation(token, new ReservationRequest(LocalDate.of(2024, 12, 25), timeId, themeId));

        ReservationStatusResponse[] responses = RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/reservations/me")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ReservationStatusResponse[].class);

        assertThat(responses).hasSize(1);
    }

    @DisplayName("사용자가 예약과 함께 결제를 한다.")
    @Disabled
    @Test
    void reserveWithPurchase() {
        MemberResponse memberResponse = fixture.registerMember(
                new MemberRegisterRequest("name", "email@mail.com", "12341234")
        ).as(MemberResponse.class);
        String token = fixture.loginAndGetToken("email@mail.com", "12341234");
        ReservationTimeResponse timeResponse = fixture.createReservationTime(15, 20);
        ThemeResponse themeResponse = fixture.createTheme(new ThemeRequest("테마", "디스크립션1", "https://theme", 1000L));

        ReservationPaymentRequest reservationPaymentRequest = new ReservationPaymentRequest(
                memberResponse.id(),
                themeResponse.id(),
                LocalDate.of(2024, 12, 25),
                timeResponse.id(),
                "paymentKet",
                "orderId");

        RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(reservationPaymentRequest)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);
    }
}
