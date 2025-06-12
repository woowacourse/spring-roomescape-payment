package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static roomescape.TestFixtures.anyPaymentWithNewId;
import static roomescape.TestFixtures.anyReservationWithNewId;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.exception.PaymentFailedException;
import roomescape.presentation.request.CreateReservationRequest;

class RoomescapeServiceTest {

    private final ReservationService reservationService = Mockito.mock(ReservationService.class);
    private final PaymentService paymentService = Mockito.mock(PaymentService.class);
    private final UserService userService = Mockito.mock(UserService.class);
    private final RoomescapeService roomescapeService = new RoomescapeService(reservationService, paymentService, userService);

    @Test
    @DisplayName("예약과 결제를 성공적으로 진행한다")
    void reserveAndPay() {
        // given
        var reservation = anyReservationWithNewId();
        var payment = anyPaymentWithNewId();

        long userId = 1L;
        LocalDate date = reservation.date();
        long timeId = reservation.timeSlot().id();
        long themeId = reservation.theme().id();
        String paymentKey = payment.paymentKey().value();
        String orderId = payment.orderId().value();
        long amount = payment.totalAmount();

        var request = new CreateReservationRequest(date, timeId, themeId, paymentKey, orderId, amount);

        when(reservationService.reserve(userId, date, timeId, themeId)).thenReturn(reservation);
        when(paymentService.pay(paymentKey, orderId, amount)).thenReturn(payment);

        // when
        var payedReservation = roomescapeService.reserveAndPay(userId, request);

        // then
        assertThat(payedReservation).isNotNull();
        assertThat(payedReservation.status()).isEqualTo(ReservationStatus.RESERVED);
        assertThat(payedReservation.paymentId()).isPresent();
    }

    @Test
    @DisplayName("결제가 실패하면 예외가 발생한다")
    void reserveAndPay_fail() {
        // given
        var reservation = anyReservationWithNewId();

        long userId = 1L;
        LocalDate date = reservation.date();
        long timeId = reservation.timeSlot().id();
        long themeId = reservation.theme().id();
        String paymentKey = "payment_key";
        String orderId = "6자미만";
        long amount = 1000L;

        var request = new CreateReservationRequest(date, timeId, themeId, paymentKey, orderId, amount);

        when(reservationService.reserve(userId, date, timeId, themeId)).thenReturn(reservation);
        when(paymentService.pay(paymentKey, orderId, amount)).thenThrow(new PaymentFailedException("결제 실패"));

        // when & then
        assertThatThrownBy(() -> roomescapeService.reserveAndPay(userId, request))
                .isInstanceOf(PaymentFailedException.class)
                .hasMessage("결제 실패");
    }
} 
