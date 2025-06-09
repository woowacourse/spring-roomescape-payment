package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import roomescape.DBHelper;
import roomescape.exception.NotFoundException;
import roomescape.member.domain.Member;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.TossPaymentCancelRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.PaymentTimeoutException;
import roomescape.payment.exception.TossPaymentException;
import roomescape.payment.infrastructure.TossRestClient;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({PaymentCancellationService.class, DBHelper.class})
class PaymentCancellationServiceTest {

    @Autowired
    private PaymentCancellationService paymentCancellationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockitoBean
    private TossRestClient tossRestClient;

    @Autowired
    DBHelper dbHelper;

    @DisplayName("결제 취소 성공 시 상태를 REFUNDED로 변경한다")
    @Test
    void cancelPayment_success() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, time, theme));
        Payment payment = dbHelper.insertCompletedPayment(reservation);

        given(tossRestClient.cancel(any(), any(TossPaymentCancelRequest.class)))
                .willReturn(mock(TossPaymentResponse.class));

        // when
        paymentCancellationService.cancelPayment(reservation.getId());

        // then
        Payment updatedPayment = paymentRepository.findByReservationId(reservation.getId()).orElseThrow();
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
    }

    @DisplayName("존재하지 않는 예약 ID인 경우 NotFoundException을 발생시킨다")
    @Test
    void cancelPayment_reservationNotFound() {
        // given
        Long nonExistentReservationId = 999L;

        // when & then
        assertThatThrownBy(() -> paymentCancellationService.cancelPayment(nonExistentReservationId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("존재하지 않는 예약입니다");
    }

    @DisplayName("Toss 결제 취소 중 타임아웃 발생 시 타임아웃 예외발생, 결제상태는 REFUND_FALIED")
    @Test
    void cancelPayment_timeout() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, time, theme));
        Payment payment = dbHelper.insertCompletedPayment(reservation);

        given(tossRestClient.cancel(any(), any(TossPaymentCancelRequest.class)))
                .willThrow(new PaymentTimeoutException("결제 취소 시간이 초과되었습니다."));

        // when & then
        assertThatThrownBy(() -> paymentCancellationService.cancelPayment(reservation.getId()))
                .isInstanceOf(PaymentTimeoutException.class)
                .hasMessageContaining("결제 취소 시간이 초과되었습니다.");

        Payment updatedPayment = paymentRepository.findByReservationId(reservation.getId()).orElseThrow();
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.REFUND_FAILED);
    }

    @DisplayName("Toss 결제 취소 실패 시 결제 상태를 REFUND_FAILED로 변경한다")
    @Test
    void cancelPayment_fail() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, time, theme));
        Payment payment = dbHelper.insertCompletedPayment(reservation);

        given(tossRestClient.cancel(any(), any(TossPaymentCancelRequest.class)))
                .willThrow(new TossPaymentException(HttpStatus.BAD_REQUEST, "결제 취소에 실패했습니다.", false));

        // when & then
        assertThatThrownBy(() -> paymentCancellationService.cancelPayment(reservation.getId()))
                .isInstanceOf(TossPaymentException.class)
                .hasMessageContaining("결제 취소에 실패했습니다.");

        Payment updatedPayment = paymentRepository.findByReservationId(reservation.getId()).orElseThrow();
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.REFUND_FAILED);
    }

    @DisplayName("결제 완료 상태가 아니라면, 해당 결제를 취소할 수 없어 예외 발생")
    @Test
    void cancelPayment_alreadyCompleted() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, time, theme));
        Payment payment = dbHelper.insertPaymentOfStatus(reservation, PaymentStatus.FAILED);

        // when & then
        assertThatThrownBy(() -> paymentCancellationService.cancelPayment(reservation.getId()))
                .isInstanceOf(TossPaymentException.class)
                .hasMessageContaining("결제 취소할 수 있는 상태가 아닙니다.");
    }

    @DisplayName("결제정보가 없는 예약의 경우, 결제취소 API를 호출하지 않고 종료한다")
    @Test
    void cancelPayment_noExistsPayment() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, time, theme));
        // 결제 정보 없음

        // when
        paymentCancellationService.cancelPayment(reservation.getId());

        // then
        verify(tossRestClient, never()).cancel(any(), any());
    }
}
