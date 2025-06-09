package roomescape.payment.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createReservationOf;
import static roomescape.TestFixture.createTimeAt_10;

import java.time.LocalDate;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.DBHelper;
import roomescape.auth.dto.LoginMember;
import roomescape.member.domain.Member;
import roomescape.member.dto.MemberResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.ReservationPaymentRequest;
import roomescape.payment.infrastructure.TossRestClient;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.ReservationService;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.dto.ThemeResponse;

@DataJpaTest
@Import({ReservationPaymentCreator.class, DBHelper.class})
class ReservationPaymentCreatorTest {

    @Autowired
    ReservationPaymentCreator reservationPaymentCreator;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    TossRestClient tossRestClient;

    @Autowired
    DBHelper dbHelper;

    @DisplayName("정상적으로 예약 후 PENDING 상태의 결제가 생성된다.")
    @Test
    void saveReservationAndPayment_success() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        LocalDate date = DEFAULT_DATE;
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        ReservationTime time = dbHelper.insertTime(createTimeAt_10());
        String paymentKey = "paymentKey";
        ReservationPaymentRequest reservationPaymentRequest = new ReservationPaymentRequest(
                date, theme.getId(), time.getId(), paymentKey, "orderId", 1000L, "paymentType");

        dbHelper.insertReservation(createReservationOf(member, date, time, theme));
        ReservationResponse fakeReservationResponse = new ReservationResponse(1L, date,
                new ReservationTimeResponse(time),
                new ThemeResponse(theme), new MemberResponse(member));
        given(reservationService.registerReservation(any())).willReturn(fakeReservationResponse);

        // when
        reservationPaymentCreator.saveReservationAndPayment(reservationPaymentRequest, LoginMember.from(member));

        // then
        SoftAssertions.assertSoftly(softly -> {
            assertThat(reservationRepository.findAll()).hasSize(1);
            assertThat(paymentRepository.findAll()).hasSize(1);
            Payment findPayment = paymentRepository.findByPaymentKey(paymentKey).orElseThrow();
            assertThat(findPayment.getStatus()).isEqualTo(PaymentStatus.PENDING);
            AssertionsForClassTypes.assertThat(findPayment.getReservation().getId()).isEqualTo(1L);
        });
    }
}
