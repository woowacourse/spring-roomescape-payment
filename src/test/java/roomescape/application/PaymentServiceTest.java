package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.exception.PaymentFailedException;

@Import({PaymentService.class, ReservationService.class})
class PaymentServiceTest extends ServiceTest {

    @Autowired
    private PaymentService service;

    @MockitoBean
    private PaymentProvider paymentProvider;

    private final PaymentRequest paymentRequest = new PaymentRequest("a", "1", 1000);

    @Test
    @DisplayName("예약에 대한 결제에 성공한다.")
    void payReservation() {
        // given
        var pendingReservation = repositoryHelper.saveAnyReservation(ReservationStatus.PENDING);
        stubPaymentProviderAlwaysSuccess();

        // when
        var payment = service.payReservation(pendingReservation.id(), paymentRequest);

        // then
        assertAll(
            () -> assertThat(payment).isNotNull(),
            () -> assertThat(payment.paymentKey()).isNotNull()
        );
    }

    @Test
    @DisplayName("보류 상태가 아닌 예약을 결제하려 하면 예외가 발생한다.")
    void payReservationNotPending() {
        // given
        var confirmedReservation = repositoryHelper.saveAnyReservation(ReservationStatus.CONFIRMED);
        stubPaymentProviderAlwaysSuccess();

        // when & then
        assertThatThrownBy(
            () -> service.payReservation(confirmedReservation.id(),paymentRequest)
        ).isInstanceOf(PaymentFailedException.class);
    }

    @Test
    @DisplayName("결제 실패 시 예외가 발생한다.")
    void failToPayReservation() {
        // given
        var pendingReservation = repositoryHelper.saveAnyReservation(ReservationStatus.PENDING);
        stubPaymentProviderAlwaysThrowsException();

        // when & then
        assertThatThrownBy(
            () -> service.payReservation(pendingReservation.id(),paymentRequest)
        ).isInstanceOf(PaymentFailedException.class);
    }

    private void stubPaymentProviderAlwaysSuccess() {
        Mockito
            .when(paymentProvider.confirm(any()))
            .thenReturn(new Payment(paymentRequest.paymentKey(), paymentRequest.amount()));
    }

    private void stubPaymentProviderAlwaysThrowsException() {
        Mockito
            .when(paymentProvider.confirm(any()))
            .thenThrow(PaymentFailedException.class);
    }
}
