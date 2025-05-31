package roomescape.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import roomescape.domain.payment.PaymentFailCode;
import roomescape.domain.payment.PaymentFailure;

class PaymentFailedExceptionTest {

    @DisplayName("예외 발생 원인이 클라이언트에게 있는 지 알 수 있다.")
    @ParameterizedTest
    @CsvSource({
        "CONDITION_NOT_SATISFIED,true",
        "INVALID_AUTH_CREDENTIALS,false",
        "EXTERNAL_PROCESSING,false"
    })
    void causedByClient(final PaymentFailCode code, final boolean expected) {
        var paymentFailure = new PaymentFailure(code, "결제 실패");
        var exception = new PaymentFailedException(paymentFailure);

        assertThat(exception.causedByClient()).isEqualTo(expected);
    }

    @DisplayName("예외 발생 원인이 서버에게 있는 지 알 수 있다.")
    @ParameterizedTest
    @CsvSource({
        "CONDITION_NOT_SATISFIED,false",
        "INVALID_AUTH_CREDENTIALS,true",
        "EXTERNAL_PROCESSING,false"
    })
    void causedByServer(final PaymentFailCode code, final boolean expected) {
        var paymentFailure = new PaymentFailure(code, "결제 실패");
        var exception = new PaymentFailedException(paymentFailure);

        assertThat(exception.causedByServer()).isEqualTo(expected);
    }

    @DisplayName("예외 발생 원인이 외부 서버에게 있는 지 알 수 있다.")
    @ParameterizedTest
    @CsvSource({
        "CONDITION_NOT_SATISFIED,false",
        "INVALID_AUTH_CREDENTIALS,false",
        "EXTERNAL_PROCESSING,true"
    })
    void causedByExternalServer(final PaymentFailCode code, final boolean expected) {
        var paymentFailure = new PaymentFailure(code, "결제 실패");
        var exception = new PaymentFailedException(paymentFailure);

        assertThat(exception.causedByExternalServer()).isEqualTo(expected);
    }
}
