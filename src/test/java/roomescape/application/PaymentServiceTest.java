package roomescape.application;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import roomescape.domain.payment.PaymentConfirmation;
import roomescape.domain.payment.PaymentDetails;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRequest;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.payment.PaymentStatusCode;
import roomescape.exception.PaymentFailedException;

class PaymentServiceTest {

    private final PaymentProvider paymentProvider = Mockito.mock(PaymentProvider.class);
    private final PaymentService paymentService = new PaymentService(paymentProvider);

    @Test
    @DisplayName("결제 성공 시 예외가 발생하지 않는다.")
    void pay() {
        // given
        var request = new PaymentRequest("a", "1", 1000);
        var paymentDetails = new PaymentDetails(new PaymentConfirmation("a", "1", "order", 1000));

        Mockito.when(paymentProvider.confirm(request)).thenReturn(paymentDetails);

        // when & then
        assertThatCode(() -> paymentService.pay("a", "1", 1000)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @DisplayName("결제 실패 시 예외가 발생한다.")
    @CsvSource({"FAILED_PAYMENT", "INVALID_AUTH_CREDENTIALS", "FAILED_INTERNAL_PROCESSING"})
    void failToPay(final PaymentStatusCode code) {
        // given
        var request = new PaymentRequest("a", "1", 1000);
        var paymentDetails = new PaymentDetails(PaymentStatus.fail(code, "결제 실패"));

        Mockito.when(paymentProvider.confirm(request)).thenReturn(paymentDetails);

        // when & then
        assertThatThrownBy(() -> paymentService.pay("a", "1", 1000))
            .isInstanceOf(PaymentFailedException.class);
    }
}
