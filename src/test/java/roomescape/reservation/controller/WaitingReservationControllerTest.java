package roomescape.reservation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createClaims;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createTimeAt;
import static roomescape.TestFixture.createWaitingOf;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import roomescape.IntegrationTest;
import roomescape.auth.infrastructure.jwt.JwtTokenProvider;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservation.dto.WaitingReservationRequest;
import roomescape.reservation.repository.WaitingReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

class WaitingReservationControllerTest extends IntegrationTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    WaitingReservationRepository waitingReservationRepository;

    @Test
    @DisplayName("사용자가 대기 예약을 생성한다")
    void createWaitingReservation() {
        // given
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Member member = dbHelper.insertMember(createDefaultMember_1());
        String token = jwtTokenProvider.createToken(createClaims(member));

        WaitingReservationRequest waitingReservationRequest = new WaitingReservationRequest(DEFAULT_DATE, time.getId(),
                theme.getId());
        // when & then
        givenWithDocs("waiting-create")
                .cookie("token", token)
                .contentType("application/json")
                .body(waitingReservationRequest)
                .when()
                .post("/waitingReservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
        assertThat(waitingReservationRepository.findAll()).hasSize(1);
    }

    @DisplayName("사용자가 본인의 특정 예약을 취소할 수 있다")
    @Test
    void deleteWaitingById() {
        // given
        LocalDate date = DEFAULT_DATE;
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Member member = dbHelper.insertMember(createDefaultMember_1());
        String token = jwtTokenProvider.createToken(createClaims(member));

        WaitingReservation waitingReservation = dbHelper.insertWaiting(createWaitingOf(member, date, time, theme));
        Long waitingId = waitingReservation.getId();

        // when & then
        givenWithDocs("waiting-deleteById")
                .cookie("token", token)
                .contentType("application/json")
                .when()
                .delete("/waitingReservations/" + waitingId)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        assertThat(waitingReservationRepository.findById(waitingId)).isEmpty();
    }
}
