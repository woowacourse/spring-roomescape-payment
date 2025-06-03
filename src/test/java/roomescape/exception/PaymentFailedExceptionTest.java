package roomescape.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaymentFailedExceptionTest {

    @Test
    @DisplayName("결제 실패 예외를 생성한다.")
    void newPaymentFailureException() {
        var exception = PaymentFailedException.byClient("결제 실패");
        assertThat(exception.getMessage()).isEqualTo("결제 실패");
    }

    @Test
    @DisplayName("예외 발생 원인이 클라이언트에게 있는 지 알 수 있다.")
    void causedByClient() {
        var exception = PaymentFailedException.byClient("결제 실패");

        assertAll(
            () -> assertThat(exception.causedByClient()).isTrue(),
            () -> assertThat(exception.causedByInternalServer()).isFalse(),
            () -> assertThat(exception.causedByExternalServer()).isFalse()
        );
    }

    @Test
    @DisplayName("예외 발생 원인이 내부 서버에게 있는 지 알 수 있다.")
    void causedByInternalServer() {
        var exception = PaymentFailedException.byServer();

        assertAll(
            () -> assertThat(exception.causedByClient()).isFalse(),
            () -> assertThat(exception.causedByInternalServer()).isTrue(),
            () -> assertThat(exception.causedByExternalServer()).isFalse()
        );
    }

    @Test
    @DisplayName("예외 발생 원인이 외부 서버에게 있는 지 알 수 있다.")
    void causedByExternalServer() {
        var exception = PaymentFailedException.byExternalServer();

        assertAll(
            () -> assertThat(exception.causedByClient()).isFalse(),
            () -> assertThat(exception.causedByInternalServer()).isFalse(),
            () -> assertThat(exception.causedByExternalServer()).isTrue()
        );
    }
}
