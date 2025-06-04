package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.domain.payment.PaymentConfirmation;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.exception.PaymentFailedException;

@Import(PaymentService.class)
class PaymentServiceTest extends ServiceTest {

    @Autowired
    private PaymentService service;

    @MockitoBean
    private PaymentProvider paymentProvider;

    @Test
    @DisplayName("예약에 대한 결제에 성공한다.")
    void pay() {
        // given
        var pendingReservation = repositoryHelper.saveAnyReservation(ReservationStatus.PENDING);

        var request = new PaymentRequest("a", "1", 1000);
        var confirmation = new PaymentConfirmation("a", "1", "order", 1000);
        Mockito.when(paymentProvider.confirm(request)).thenReturn(confirmation);

        // when
        service.pay(pendingReservation.id(),"a", "1", 1000);

        // then
        var confirmedReservation = repositoryHelper.findReservation(pendingReservation.id());
        assertAll(
            () -> assertThat(confirmedReservation.isConfirmed()).isTrue(),
            () -> assertThat(confirmedReservation.payment().paymentKey()).isEqualTo("a")
        );
    }

    @Test
    @DisplayName("보류 상태가 아닌 예약을 결제하려 하면 예외가 발생한다.")
    void payNotPendingReservation() {
        // given
        var confirmedReservation = repositoryHelper.saveAnyReservation(ReservationStatus.CONFIRMED);

        // when & then
        assertThatThrownBy(() -> service.pay(confirmedReservation.id(),"a", "1", 1000))
            .isInstanceOf(PaymentFailedException.class);
    }

    @Test
    @DisplayName("결제 실패 시 예외가 발생한다.")
    void failToPay() {
        // given
        var reservation = repositoryHelper.saveAnyReservation(ReservationStatus.PENDING);

        var request = new PaymentRequest("a", "1", 1000);
        Mockito.when(paymentProvider.confirm(request)).thenThrow(PaymentFailedException.class);

        // when & then
        assertThatThrownBy(() -> service.pay(reservation.id(),"a", "1", 1000))
            .isInstanceOf(PaymentFailedException.class);
    }
}
