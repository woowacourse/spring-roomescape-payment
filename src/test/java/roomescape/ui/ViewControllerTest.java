package roomescape.ui;

import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createClaims;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createReservationOf;
import static roomescape.TestFixture.createTimeAt_10;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import roomescape.IntegrationTest;
import roomescape.TestFixture;
import roomescape.auth.infrastructure.jwt.JwtTokenProvider;
import roomescape.member.domain.Member;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;

class ViewControllerTest extends IntegrationTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @DisplayName("본인의 예약이라면 해당 예약의 결제 페이지로 접근 시 정상적으로 OK 반환")
    @Test
    void getMyPage_success() {
        // given
        Member member = dbHelper.insertMember(TestFixture.createDefaultMember_1());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, createTimeAt_10(), createDefaultTheme()));
        Payment payment = dbHelper.insertCompletedPayment(reservation);

        String token = jwtTokenProvider.createToken(createClaims(member));

        // when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/mypage/" + reservation.getId() + "/payment")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("본인의 예약이 아니라면 해당 예약의 결제 페이지로 접근할 수 없다.")
    @Test
    void getMyPage_forbidden() {
        // given
        Member member = dbHelper.insertMember(TestFixture.createMemberByName("회원"));
        Member otherMember = dbHelper.insertMember(TestFixture.createMemberByName("다른 회원"));
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, createTimeAt_10(), createDefaultTheme()));
        Payment payment = dbHelper.insertCompletedPayment(reservation);

        // 다른 회원으로 토큰 생성
        String token = jwtTokenProvider.createToken(createClaims(otherMember));

        // when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/mypage/" + reservation.getId() + "/payment")
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

}
