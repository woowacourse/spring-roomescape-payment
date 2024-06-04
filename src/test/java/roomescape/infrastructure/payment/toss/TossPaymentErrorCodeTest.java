package roomescape.infrastructure.payment.toss;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class TossPaymentErrorCodeTest {

    @DisplayName("정의해놓은 에러코드에 포함될 경우 매핑된 코드를 반환한다.")
    @Test
    void return_error_code_when_defined_code() {
        TossPaymentErrorCode tossPaymentErrorCode = TossPaymentErrorCode.find("EXCEED_MAX_PAYMENT_AMOUNT");

        assertAll(
                () -> assertThat(tossPaymentErrorCode.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(tossPaymentErrorCode.getMessage()).isEqualTo("하루 결제 가능 금액을 초과했습니다.")
        );
    }

    @DisplayName("정의해놓은 에러코드에 포함되지 않으면 SERVER ERROR를 반환한다.")
    @Test
    void return_error_code_when_not_defined_code() {
        TossPaymentErrorCode tossPaymentErrorCode = TossPaymentErrorCode.find("UNAUTHORIZED_KEY");

        assertAll(
                () -> assertThat(tossPaymentErrorCode.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR),
                () -> assertThat(tossPaymentErrorCode.getMessage()).isEqualTo("결제에 실패했습니다.")
        );
    }
}
