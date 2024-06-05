package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.retry.annotation.EnableRetry;
import roomescape.component.TossPaymentClient;
import roomescape.domain.payment.Payment;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.dto.payment.PaymentDto;
import roomescape.exception.TossPaymentsException;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static roomescape.TestFixture.*;
import static roomescape.TestFixture.THEME_HORROR;
import static roomescape.exception.RoomescapeExceptionCode.DATABASE_SAVE_ERROR;
import static roomescape.exception.RoomescapeExceptionCode.TOSS_PAYMENTS_ERROR;

@EnableRetry
@SpringBootTest
class PaymentServiceTest {

    @MockBean
    TossPaymentClient paymentClient;

    @MockBean
    private PaymentRepository paymentRepository;

    @MockBean
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentService paymentService;

    @Test
    @DisplayName("처음 승인 요청 시도에서는 예외를 던지고, 이후 시도에서는 성공한 뒤 결제 정보를 저장한다.")
    void confirmPaymentWithRetries() {
        // given
        final Long reservationId = 1L;
        final Member member = MEMBER_TENNY(1L);
        final LocalDate date = DATE_MAY_EIGHTH;
        final ReservationTime time = RESERVATION_TIME_SIX(1L);
        final Theme theme = THEME_HORROR(1L);
        final Reservation reservation = new Reservation(member, date, time, theme, ReservationStatus.RESERVED);
        final PaymentDto paymentDto = new PaymentDto(PAYMENT_KEY, ORDER_ID, AMOUNT);
        given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));
        willThrow(new TossPaymentsException(TOSS_PAYMENTS_ERROR.getHttpStatusCode(), TOSS_PAYMENTS_ERROR.getMessage()))
                .willThrow(new TossPaymentsException(TOSS_PAYMENTS_ERROR.getHttpStatusCode(), TOSS_PAYMENTS_ERROR.getMessage()))
                .willDoNothing()
                .given(paymentClient).confirm(paymentDto);

        // when
        paymentService.confirmPayment(paymentDto, reservationId);

        // then
        verify(paymentClient, times(3)).confirm(paymentDto);
        then(paymentRepository).should().save(any(Payment.class));
    }

    @Test
    @DisplayName("모든 승인 요청 시도에서 예외를 던진다.")
    void confirmPaymentWithMaxRetries() {
        // given
        final Long reservationId = 1L;
        final Member member = MEMBER_TENNY(1L);
        final LocalDate date = DATE_MAY_EIGHTH;
        final ReservationTime time = RESERVATION_TIME_SIX(1L);
        final Theme theme = THEME_HORROR(1L);
        final Reservation reservation = new Reservation(member, date, time, theme, ReservationStatus.RESERVED);
        final PaymentDto paymentDto = new PaymentDto(PAYMENT_KEY, ORDER_ID, AMOUNT);
        given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));
        willThrow(new TossPaymentsException(TOSS_PAYMENTS_ERROR.getHttpStatusCode(), TOSS_PAYMENTS_ERROR.getMessage()))
                .given(paymentClient).confirm(paymentDto);

        // when
        assertThatThrownBy(() -> paymentService.confirmPayment(paymentDto, reservationId))
                .isInstanceOf(TossPaymentsException.class);

        // then
        verify(paymentClient, times(3)).confirm(paymentDto);
        then(paymentRepository).should(never()).save(any(Payment.class));
    }
    
    @Test
    @DisplayName("결제 승인은 성공했으나 결제 정보 저장이 실패한 경우 결제를 취소한다.")
    void cancelPaymentWhenSaveFailure() {
        // given
        final Long reservationId = 1L;
        final Member member = MEMBER_TENNY(1L);
        final LocalDate date = DATE_MAY_EIGHTH;
        final ReservationTime time = RESERVATION_TIME_SIX(1L);
        final Theme theme = THEME_HORROR(1L);
        final Reservation reservation = new Reservation(member, date, time, theme, ReservationStatus.RESERVED);
        final PaymentDto paymentDto = new PaymentDto(PAYMENT_KEY, ORDER_ID, AMOUNT);
        final String errorMessage = DATABASE_SAVE_ERROR.getMessage();
        given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));
        willDoNothing().given(paymentClient).confirm(paymentDto);
        willThrow(new RuntimeException(errorMessage))
                .given(paymentRepository).save(any(Payment.class));
        willDoNothing().given(paymentClient).cancel(paymentDto, errorMessage);

        // when
        paymentService.confirmPayment(paymentDto, reservationId);

        // then
        then(paymentClient).should(times(1)).confirm(paymentDto);
        then(paymentRepository).should(times(1)).save(any(Payment.class));
        then(paymentClient).should(times(1)).cancel(paymentDto, errorMessage);
    }
}
