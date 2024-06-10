package roomescape.payment.exception.toss;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.exception.ErrorType;
import roomescape.exception.InternalException;
import roomescape.exception.PaymentException;
import roomescape.payment.exception.TossPaymentErrorResponse;
import roomescape.payment.exception.TossPaymentException;

@DisplayName("토스 결제 예외 테스트")
class TossPaymentExceptionTest {

    @DisplayName("토스 결제 예외 발생 시, 예외가 변환되어 발생한다.")
    @Test
    void exception() {
        // given
        String errorCode = "ALREADY_PROCESSED_PAYMENT";
        String message = "이미 처리된 결제 입니다.";

        TossPaymentErrorResponse tossPaymentErrorResponse = new TossPaymentErrorResponse(errorCode, message);

        // when, then
        assertThatThrownBy(() -> {
            throw new TossPaymentException(tossPaymentErrorResponse);
        }).isInstanceOf(PaymentException.class)
                .hasFieldOrPropertyWithValue("code", errorCode)
                .hasFieldOrPropertyWithValue("message", message);
    }

    @DisplayName("토스 결제 예외 발생 시, 예외가 노출되지 않는다.")
    @Test
    void noExpose() {
        // given
        String errorCode = "UNAUTHORIZED_KEY";
        String message = "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다.";

        TossPaymentErrorResponse tossPaymentErrorResponse = new TossPaymentErrorResponse(errorCode, message);

        // when, then
        assertThatThrownBy(() -> {
            throw new TossPaymentException(tossPaymentErrorResponse);
        }).isInstanceOf(InternalException.class)
                .hasFieldOrPropertyWithValue("code", ErrorType.PAYMENT_ERROR.getErrorCode())
                .hasFieldOrPropertyWithValue("message", ErrorType.PAYMENT_ERROR.getMessage());
    }
}
