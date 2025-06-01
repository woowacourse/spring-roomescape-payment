package roomescape.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.domain.payment.PaymentFailCode;

class PaymentFailedExceptionTest {

    @Test
    @DisplayName("예외 발생 원인이 클라이언트에게 있는 지 알 수 있다.")
    void causedByClient() {
        var exception = new PaymentFailedException(PaymentFailCode.CONDITION_NOT_SATISFIED, "결제 실패");

        assertThat(exception.causedByClient()).isTrue();
    }

    @Test
    @DisplayName("예외 발생 원인이 서버에게 있는 지 알 수 있다.")
    void causedByServer() {
        var exception = new PaymentFailedException(PaymentFailCode.INVALID_AUTH_CREDENTIALS, "키 인증 실패");

        assertThat(exception.causedByServer()).isTrue();
    }

    @Test
    @DisplayName("예외 발생 원인이 외부 서버에게 있는 지 알 수 있다.")
    void causedByExternalServer() {
        var exception = new PaymentFailedException(PaymentFailCode.EXTERNAL_SERVER_PROCESSING, "외부 서버 결제 실패");

        assertThat(exception.causedByExternalServer()).isTrue();
    }
}
