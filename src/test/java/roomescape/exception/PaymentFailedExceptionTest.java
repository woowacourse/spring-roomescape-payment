package roomescape.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.exception.PaymentFailedException.Cause;

class PaymentFailedExceptionTest {

    @Test
    @DisplayName("결제 실패 예외를 생성한다.")
    void newPaymentFailureException() {
        var exception = new PaymentFailedException(Cause.CLIENT_ERROR, "결제 실패");
        assertThat(exception.getMessage()).isEqualTo("결제 실패");
    }

    @Test
    @DisplayName("예외 발생 원인이 클라이언트에게 있는 지 알 수 있다.")
    void causedByClient() {
        var exception = new PaymentFailedException(Cause.CLIENT_ERROR, "결제 실패");

        assertThat(exception.causedByClient()).isTrue();
    }

    @Test
    @DisplayName("예외 발생 원인이 서버에게 있는 지 알 수 있다.")
    void causedByServer() {
        var exception = new PaymentFailedException(Cause.SERVER_ERROR, "키 인증 실패");

        assertThat(exception.causedByServer()).isTrue();
    }

    @Test
    @DisplayName("예외 발생 원인이 외부 서버에게 있는 지 알 수 있다.")
    void causedByExternalServer() {
        var exception = new PaymentFailedException(Cause.EXTERNAL_ERROR, "외부 서버 결제 실패");

        assertThat(exception.causedByExternalServer()).isTrue();
    }
}
