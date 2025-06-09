package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createReservationOf;
import static roomescape.TestFixture.createTimeAt;

import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.DBHelper;
import roomescape.member.domain.Member;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.infrastructure.TossRestClient;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@DataJpaTest
@Import({TossPaymentEventProcessor.class, PaymentCancellationService.class, DBHelper.class})
class TossPaymentEventProcessorTest {

    @Autowired
    private TossPaymentEventProcessor tossPaymentEventProcessor;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @MockitoBean
    private TossRestClient tossRestClient;

    @MockitoBean
    PaymentCancellationService paymentCancellationService;

    @Autowired
    private DBHelper dbHelper;

    @DisplayName("예약 정보를 기반으로 NOT_PAID 상태의 결제를 생성하고 저장한다")
    @Test
    void saveNotPaidPayment() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, time, theme));

        // when
        tossPaymentEventProcessor.saveNotPaidPayment(reservation);

        // then
        Payment payment = paymentRepository.findByReservationId(reservation.getId()).orElseThrow();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.NOT_PAID);
        assertThat(payment.getReservation().getId()).isEqualTo(reservation.getId());
        assertThat(payment.getMember()).isEqualTo(member);
    }

    @DisplayName("예약 ID로 결제를 찾아 결제 취소 서비스로 취소 요청을 보낸다.")
    @Test
    void cancelPayment_success() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, time, theme));
        Payment payment = dbHelper.insertNotPaidPayment(reservation);

        TossPaymentResponse mockResponse = mock(TossPaymentResponse.class);
        given(mockResponse.status()).willReturn("CANCELED");
        given(tossRestClient.cancel(any(), any())).willReturn(mockResponse);

        // when
        Long reservationId = reservation.getId();
        tossPaymentEventProcessor.refundPayment(reservationId);

        // then
        verify(paymentCancellationService).cancelPayment(reservationId);
    }
}
