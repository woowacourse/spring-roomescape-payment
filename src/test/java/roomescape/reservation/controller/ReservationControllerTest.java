package roomescape.reservation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createAdminMember;
import static roomescape.TestFixture.createClaims;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createReservation_1;
import static roomescape.TestFixture.createReservation_2;
import static roomescape.TestFixture.createTimeAt_10;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import roomescape.IntegrationTest;
import roomescape.TestFixture;
import roomescape.auth.infrastructure.jwt.JwtTokenProvider;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.WaitingReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

class ReservationControllerTest extends IntegrationTest {

    @Autowired
    ReservationTimeRepository timeRepository;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    WaitingReservationRepository waitingReservationRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Test
    void 유저_예약_생성_성공() {
        // given
        Member member1 = TestFixture.createDefaultMember_1();
        ReservationTime reservationTimeAt10 = createTimeAt_10();
        Theme theme1 = createDefaultTheme();
        dbHelper.insertMember(member1);
        dbHelper.insertTime(reservationTimeAt10);
        dbHelper.insertTheme(theme1);
        String token = jwtTokenProvider.createToken(createClaims(member1));

        ReservationRequest reservationRequest = new ReservationRequest(
                DEFAULT_DATE,
                reservationTimeAt10.getId(),
                theme1.getId()
        );

        // when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(reservationRequest)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void 예약_조회_성공() {
        // given
        Member adminMember = createAdminMember("관리자", "admin@naver.com", "1234");
        dbHelper.insertMember(adminMember);
        String token = jwtTokenProvider.createToken(createClaims(adminMember));

        dbHelper.insertReservation(createReservation_1());
        dbHelper.insertReservation(createReservation_2());

        // when & then
        List<ReservationResponse> responses = RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .extract().jsonPath().getList(".", ReservationResponse.class);

        assertThat(responses).hasSize(2);
    }

    @Test
    void 예약_삭제_성공() {
        // given
        Member adminMember = createAdminMember("관리자", "admin@naver.com", "1234");
        dbHelper.insertMember(adminMember);
        String token = jwtTokenProvider.createToken(createClaims(adminMember));

        Reservation reservation = dbHelper.insertReservation(createReservation_1());

        // when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().delete("/reservations/" + reservation.getId())
                .then().log().all()
                .statusCode(204);
    }
}
