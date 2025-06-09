package roomescape.reservation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createAdminMember;
import static roomescape.TestFixture.createClaims;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createReservation_1;
import static roomescape.TestFixture.createReservation_2;
import static roomescape.TestFixture.createTimeAt_10;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.IntegrationTest;
import roomescape.auth.infrastructure.jwt.JwtTokenProvider;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.infrastructure.TossRestClient;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.WaitingReservationRepository;
import roomescape.reservation.service.dto.CreateRegistrationCommand;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

class AdminReservationControllerTest extends IntegrationTest {

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
    void 관리자_예약_생성_성공() {
        // given
        ReservationTime reservationTime = dbHelper.insertTime(createTimeAt_10());
        Theme theme = dbHelper.insertTheme(createDefaultTheme());

        Member admin = dbHelper.insertMember(createAdminMember("관리자", "admin@naver.com", "1234"));
        String token = jwtTokenProvider.createToken(createClaims(admin));

        CreateRegistrationCommand request = new CreateRegistrationCommand(
                admin.getId(),
                DEFAULT_DATE,
                reservationTime.getId(),
                theme.getId()
        );

        // when & then
        ReservationResponse response = givenWithDocs("admin-reservation-create")
                .cookie("token", token)
                .contentType("application/json")
                .body(request)
                .when()
                .post("/admin/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(ReservationResponse.class);

        assertAll(
                () -> assertThat(response.id()).isNotNull(),
                () -> assertThat(response.member().name()).isEqualTo(admin.getName())
        );
    }

    @Test
    void 관리자_예약_조회_성공() {
        // given
        Member adminMember = dbHelper.insertMember(createAdminMember("관리자", "admin@naver.com", "1234"));
        String token = jwtTokenProvider.createToken(createClaims(adminMember));

        dbHelper.insertReservation(createReservation_1());
        dbHelper.insertReservation(createReservation_2());

        // when & then
        List<ReservationResponse> responses = givenWithDocs("admin-reservation-get")
                .cookie("token", token)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .extract().jsonPath().getList(".", ReservationResponse.class);

        assertThat(responses).hasSize(2);
    }

    @Test
    void 관리자_예약_삭제_성공() {
        // given
        Member adminMember = createAdminMember("관리자", "admin@naver.com", "1234");
        dbHelper.insertMember(adminMember);
        String token = jwtTokenProvider.createToken(createClaims(adminMember));

        Reservation reservation = dbHelper.insertReservation(createReservation_1());
        Payment payment = dbHelper.insertCompletedPayment(reservation);

        TossPaymentResponse mockResponse = mock(TossPaymentResponse.class);
        given(mockResponse.status()).willReturn("CANCELED");
        String paymentKey = payment.getPaymentKey();
        given(tossRestClient.cancel(ArgumentMatchers.eq(paymentKey), any()))
                .willReturn(mockResponse);

        // when & then
        givenWithDocs("admin-reservation-delete")
                .cookie("token", token)
                .when().delete("/admin/reservations/" + reservation.getId())
                .then().log().all()
                .statusCode(204);
    }

}
