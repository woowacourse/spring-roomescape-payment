package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
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
import roomescape.exception.NotFoundException;
import roomescape.member.domain.Member;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.TossPaymentException;
import roomescape.payment.infrastructure.TossRestClient;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@DataJpaTest
@Import({TossPaymentEventProcessor.class, DBHelper.class})
class TossPaymentEventProcessorTest {

    @Autowired
    private TossPaymentEventProcessor tossPaymentEventProcessor;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @MockitoBean
    private TossRestClient tossRestClient;

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
        assertThat(payment.getReservation()).isEqualTo(reservation);
        assertThat(payment.getMember()).isEqualTo(member);
    }

    @DisplayName("예약 ID로 결제를 찾아 Toss 결제 취소 요청을 성공적으로 수행하고 상태를 REFUNDED로 변경한다")
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
        tossPaymentEventProcessor.cancelPayment(reservation.getId());

        // then
        Payment updatedPayment = paymentRepository.findByReservationId(reservation.getId()).orElseThrow();
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
    }

    @DisplayName("존재하지 않는 예약 ID이면 NotFoundException을 던진다")
    @Test
    void cancelPayment_notFoundReservation() {
        // given
        Long notExistReservationId = 999L;

        // when & then
        assertThatThrownBy(() -> tossPaymentEventProcessor.cancelPayment(notExistReservationId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("존재하지 않는 예약입니다");
    }

    @DisplayName("예약 ID에 해당하는 결제가 없으면 NotFoundException을 던진다")
    @Test
    void cancelPayment_notFoundPayment() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, time, theme));

        // when & then
        assertThatThrownBy(() -> tossPaymentEventProcessor.cancelPayment(reservation.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("reservationId에 해당하는 결제를 찾을 수 없습니다");
    }

    @DisplayName("Toss 결제 취소 응답이 실패 상태이면 TossPaymentException을 던진다")
    @Test
    void cancelPayment_tossFail() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, time, theme));
        Payment payment = dbHelper.insertNotPaidPayment(reservation);

        TossPaymentResponse mockResponse = mock(TossPaymentResponse.class);
        given(mockResponse.status()).willReturn("FAILED");
        given(tossRestClient.cancel(any(), any())).willReturn(mockResponse);

        // when & then
        assertThatThrownBy(() -> tossPaymentEventProcessor.cancelPayment(reservation.getId()))
                .isInstanceOf(TossPaymentException.class)
                .hasMessageContaining("결제 취소 실패");
    }
}
