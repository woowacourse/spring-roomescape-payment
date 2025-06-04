package roomescape.reservation.controller;

import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createClaims;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createTimeAt_10;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
import roomescape.reservation.dto.ReservationRequest;
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
}
