package roomescape.application;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import roomescape.domain.payment.PaymentConfirmation;
import roomescape.domain.payment.PaymentExecutionResult;
import roomescape.domain.payment.PaymentProvider;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.payment.PaymentRequest;
import roomescape.domain.payment.TransactionStatus;
import roomescape.domain.payment.TransactionStatusCode;
import roomescape.exception.PaymentFailedException;
import roomescape.exception.PaymentInternalException;

class PaymentServiceTest {

    private final PaymentProvider paymentProvider = Mockito.mock(PaymentProvider.class);
    private final PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);
    private final PaymentService paymentService = new PaymentService(paymentProvider, paymentRepository);

    @Test
    @DisplayName("결제 성공 시 예외가 발생하지 않는다.")
    void pay() {
        // given
        var request = new PaymentRequest("a", "123456", 1000);
        var paymentDetails = new PaymentExecutionResult(new PaymentConfirmation("a", "123456", 1000, "DONE"));

        Mockito.when(paymentProvider.confirm(request)).thenReturn(paymentDetails);

        // when & then
        assertThatCode(() -> paymentService.pay("a", "123456", 1000)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @DisplayName("결제 실패 시 예외가 발생한다.")
    @MethodSource("failToPaySource")
    void failToPay(final TransactionStatusCode code, final Class<?> expectedException) {
        // given
        var request = new PaymentRequest("a", "123456", 1000);
        var paymentDetails = new PaymentExecutionResult(TransactionStatus.fail(code, "결제 실패"));

        Mockito.when(paymentProvider.confirm(request)).thenReturn(paymentDetails);

        // when & then
        assertThatThrownBy(() -> paymentService.pay("a", "123456", 1000)).isInstanceOf(expectedException);
    }

    private static Stream<Arguments> failToPaySource() {
        return Stream.of(
            Arguments.of(TransactionStatusCode.FAILED_PAYMENT, PaymentFailedException.class),
            Arguments.of(TransactionStatusCode.INVALID_AUTH_CREDENTIALS, PaymentInternalException.class),
            Arguments.of(TransactionStatusCode.FAILED_INTERNAL_PROCESSING, PaymentInternalException.class)
        );
    }
}
