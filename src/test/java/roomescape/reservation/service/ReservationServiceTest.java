package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createReservation_1;
import static roomescape.TestFixture.createReservation_2;
import static roomescape.TestFixture.createTimeAt_10;
import static roomescape.constant.TestData.RESERVATION_COUNT;

import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import roomescape.DBHelper;
import roomescape.IntegrationTest;
import roomescape.auth.dto.LoginMember;
import roomescape.member.domain.Member;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.infrastructure.TossRestClient;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSearchRequest;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.dto.CreateRegistrationCommand;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@Transactional
class ReservationServiceTest extends IntegrationTest {

    @Autowired
    DBHelper dbHelper;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationService service;

    @MockitoBean
    TossRestClient tossRestClient;

    @Test
    void 모든_예약을_조회한다() {
        // given
        dbHelper.insertReservation(createReservation_1());
        dbHelper.insertReservation(createReservation_2());

        // when
        List<ReservationResponse> responses = service.findReservationsByCriteria(
                new ReservationSearchRequest(null, null, null, null));

        // then
        assertThat(responses).hasSize(RESERVATION_COUNT)
                .extracting(ReservationResponse::id)
                .doesNotContain(0L);
    }

//    @Test
//    void 지나간_날짜와_시간이면_예외가_발생한다() {
//        // given
//        ReservationTime time = dbHelper.insertTime(createTimeAt_10());
//        Theme theme = dbHelper.insertTheme(createDefaultTheme());
//        LocalDate pastDate = LocalDate.now().minusDays(1);
//        ReservationRequest request = new ReservationRequest(pastDate, time.getId(), theme.getId());
//
//        Member member = dbHelper.insertMember(createDefaultMember_1());
//        LoginMember loginMember = LoginMember.from(member);
//
//        // when & then
//        assertThatThrownBy(() -> service.resisterReservation(request, loginMember))
//                .isInstanceOf(ReservationException.class)
//                .hasMessage("예약은 현재 시간 이후로 가능합니다.");
//    }

    @Test
    void 새로운_예약은_정상_생성된다() {
        // given
        ReservationTime time = dbHelper.insertTime(createTimeAt_10());
        Theme theme = dbHelper.insertTheme(createDefaultTheme());

        Member member = dbHelper.insertMember(createDefaultMember_1());
        LoginMember loginMember = LoginMember.from(member);

        // when
        ReservationResponse result = service.registerReservation(
                new CreateRegistrationCommand(loginMember.id(), DEFAULT_DATE, time.getId(), theme.getId())
        );

        // then
        SoftAssertions.assertSoftly(soft -> {
            assertThat(reservationRepository.findAll()).hasSize(1);
            assertThat(result.date()).isEqualTo(DEFAULT_DATE);
            assertThat(result.time().startAt()).isEqualTo(time.getStartAt());
            assertThat(result.theme().name()).isEqualTo(theme.getName());
        });
    }

    @Test
    void 예약을_삭제한다() {
        // given
        Reservation reservation = dbHelper.insertReservation(createReservation_1());
        Payment payment = dbHelper.insertCompletedPayment(reservation);

        assertThat(reservationRepository.findAll()).hasSize(1);

        TossPaymentResponse mockResponse = mock(TossPaymentResponse.class);
        given(mockResponse.status()).willReturn("CANCELED");
        String paymentKey = payment.getPaymentKey();
        given(tossRestClient.cancel(ArgumentMatchers.eq(paymentKey), any()))
                .willReturn(mockResponse);

        // when
        service.deleteById(reservation.getId());

        // then
        Reservation findReservation = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(findReservation.getReservationStatus()).isEqualTo(ReservationStatus.CANCELED);
    }
}
