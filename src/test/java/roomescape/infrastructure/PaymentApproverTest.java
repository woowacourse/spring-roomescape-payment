package roomescape.infrastructure;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.utils.TestClient.RESPONSE_4XX_KEY;
import static roomescape.utils.TestClient.RESPONSE_5XX_KEY;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.core.dto.payment.PaymentConfirmRequest;
import roomescape.core.dto.reservation.ReservationPaymentRequest;
import roomescape.exception.PaymentException;
import roomescape.utils.TestClient;
import roomescape.utils.TestFixture;

class PaymentApproverTest {
    private static final String TOMORROW = TestFixture.getTomorrowDate();

    private final PaymentApprover paymentApprover
            = new PaymentApprover(new TestClient(), new PaymentSecretKeyEncoder());

    @Test
    @DisplayName("결제를 승인하는 과정에서 4XX 에러가 발생하면, PaymentException을 발생시킨다.")
    void confirmPaymentException4XX() {
        ReservationPaymentRequest reservationPaymentRequest =
                new ReservationPaymentRequest(TOMORROW, 1L, 1L, RESPONSE_4XX_KEY, "1", 1L);
        PaymentConfirmRequest request = new PaymentConfirmRequest(reservationPaymentRequest);

        assertThatThrownBy(() -> paymentApprover.confirmPayment(request))
                .isInstanceOf(PaymentException.class);
    }

    @Test
    @DisplayName("결제를 승인하는 과정에서 5XX 에러가 발생하면, PaymentException을 발생시킨다.")
    void confirmPaymentException5XX() {
        ReservationPaymentRequest reservationPaymentRequest =
                new ReservationPaymentRequest(TOMORROW, 1L, 1L, RESPONSE_5XX_KEY, "1", 1L);
        PaymentConfirmRequest request = new PaymentConfirmRequest(reservationPaymentRequest);

        assertThatThrownBy(() -> paymentApprover.confirmPayment(request))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 서버와의 연결이 원활하지 않습니다. 잠시 후 다시 시도해 주세요.");
    }
}
