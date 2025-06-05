package roomescape.reservation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.createAdminMember;
import static roomescape.TestFixture.createClaims;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createTimeAt;
import static roomescape.TestFixture.createWaitingOf;
import static roomescape.TestFixture.DEFAULT_DATE;

import io.restassured.RestAssured;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.IntegrationTest;
import roomescape.auth.infrastructure.jwt.JwtTokenProvider;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.repository.WaitingReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

class AdminWaitingReservationControllerTest extends IntegrationTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private WaitingReservationRepository waitingReservationRepository;

    @Test
    void 관리자_예약대기_조회_성공() {
        // given
        Member adminMember = createAdminMember("관리자", "admin@naver.com", "1234");
        dbHelper.insertMember(adminMember);
        String token = jwtTokenProvider.createToken(createClaims(adminMember));

        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        WaitingReservation waitingReservation = dbHelper.insertWaiting(
            createWaitingOf(member, DEFAULT_DATE, time, theme)
        );

        // when & then
        List<ReservationResponse> responses = RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/admin/waitings")
                .then().log().all()
                .statusCode(200)
                .extract().jsonPath().getList(".", ReservationResponse.class);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).id()).isEqualTo(waitingReservation.getId());
    }

    @Test
    void 관리자_예약대기_승인_성공() {
        // given
        Member adminMember = createAdminMember("관리자", "admin@naver.com", "1234");
        dbHelper.insertMember(adminMember);
        String token = jwtTokenProvider.createToken(createClaims(adminMember));

        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        WaitingReservation waitingReservation = dbHelper.insertWaiting(
            createWaitingOf(member, DEFAULT_DATE, time, theme)
        );

        // when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().patch("/admin/waitings/" + waitingReservation.getId())
                .then().log().all()
                .statusCode(200);

        assertThat(waitingReservationRepository.findById(waitingReservation.getId())).isEmpty();
    }

    @Test
    void 관리자_예약대기_거절_성공() {
        // given
        Member adminMember = createAdminMember("관리자", "admin@naver.com", "1234");
        dbHelper.insertMember(adminMember);
        String token = jwtTokenProvider.createToken(createClaims(adminMember));

        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        WaitingReservation waitingReservation = dbHelper.insertWaiting(
            createWaitingOf(member, DEFAULT_DATE, time, theme)
        );

        // when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().delete("/admin/waitings/" + waitingReservation.getId())
                .then().log().all()
                .statusCode(204);

        assertThat(waitingReservationRepository.findById(waitingReservation.getId())).isEmpty();
    }
}
