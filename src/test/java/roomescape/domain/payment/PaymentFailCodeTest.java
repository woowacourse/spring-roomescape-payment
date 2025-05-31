package roomescape.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import roomescape.domain.payment.PaymentFailCode.Cause;

class PaymentFailCodeTest {

    @ParameterizedTest
    @DisplayName("실패 코드의 원인을 알 수 있다.")
    @CsvSource({
        "CONDITION_NOT_SATISFIED,CLIENT_ERROR",
        "INVALID_AUTH_CREDENTIALS,SERVER_ERROR",
        "EXTERNAL_PROCESSING,EXTERNAL_ERROR"
    })
    void causedBy(final PaymentFailCode failCode, final Cause expectedCause) {
        assertThat(failCode.causedBy(expectedCause)).isTrue();
    }
}
