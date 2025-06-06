package roomescape.reservation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createClaims;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createReservationOf;
import static roomescape.TestFixture.createTimeAt;
import static roomescape.TestFixture.createTimeAt_10;

import io.restassured.http.ContentType;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.IntegrationTest;
import roomescape.TestFixture;
import roomescape.auth.infrastructure.jwt.JwtTokenProvider;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.infrastructure.TossRestClient;
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

    @MockitoBean
    TossRestClient tossRestClient;

    @DisplayName("유저 본인의 예약 조회 성공")
    @Test
    void getReservationById() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        String token = jwtTokenProvider.createToken(createClaims(member));

        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(
            createReservationOf(member, DEFAULT_DATE, time, theme)
        );

        // when & then
        ReservationResponse response = givenWithDocs("reservation-getById")
                .cookie("token", token)
                .when().get("/reservations/" + reservation.getId())
                .then().log().all()
                .statusCode(200)
                .extract().as(ReservationResponse.class);

        assertThat(response.id()).isEqualTo(reservation.getId());
        assertThat(response.date()).isEqualTo(DEFAULT_DATE);
        assertThat(response.time().startAt()).isEqualTo(time.getStartAt());
        assertThat(response.theme().name()).isEqualTo(theme.getName());
        assertThat(response.member().name()).isEqualTo(member.getName());
    }

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
        givenWithDocs("reservation-create")
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(reservationRequest)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }
}
