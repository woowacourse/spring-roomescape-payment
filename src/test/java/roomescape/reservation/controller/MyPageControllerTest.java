package roomescape.reservation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createClaims;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createTimeAt;
import static roomescape.TestFixture.createWaitingOf;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.IntegrationTest;
import roomescape.auth.infrastructure.jwt.JwtTokenProvider;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservation.dto.MyRegistrationResponse;
import roomescape.reservation.repository.WaitingReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

class MyPageControllerTest extends IntegrationTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private WaitingReservationRepository waitingReservationRepository;

    @DisplayName("나의 예약 + 대기 통합 조회 성공")
    @Test
    void getMyRegistrations() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        String token = jwtTokenProvider.createToken(createClaims(member));

        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        WaitingReservation waitingReservation = dbHelper.insertWaiting(
            createWaitingOf(member, DEFAULT_DATE, time, theme)
        );

        // when & then
        List<MyRegistrationResponse> responses = givenWithDocs("mypage-registrations-get")
                .cookie("token", token)
                .when().get("/mypage/registrations")
                .then().log().all()
                .statusCode(200)
                .extract().jsonPath().getList(".", MyRegistrationResponse.class);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).reservationId()).isEqualTo(waitingReservation.getId());
        assertThat(responses.get(0).reservationStatus()).isEqualTo("예약 대기");
    }

    @DisplayName("나의 특정 예약대기 취소 성공")
    @Test
    void deleteWaiting() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        String token = jwtTokenProvider.createToken(createClaims(member));

        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        WaitingReservation waitingReservation = dbHelper.insertWaiting(
            createWaitingOf(member, DEFAULT_DATE, time, theme)
        );

        // when & then
        givenWithDocs("mypage-waitings-delete")
                .cookie("token", token)
                .when().delete("/mypage/waitings/" + waitingReservation.getId())
                .then().log().all()
                .statusCode(204);

        assertThat(waitingReservationRepository.findById(waitingReservation.getId())).isEmpty();
    }
}
