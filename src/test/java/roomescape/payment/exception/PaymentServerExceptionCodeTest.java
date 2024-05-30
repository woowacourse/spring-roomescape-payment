package roomescape.payment.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentServerExceptionCodeTest {
    @Test
    @DisplayName("특정 에러 코드는 서버 에러로 간주한다.")
    void specific_error_code_is_server_error(){
        String errorCode = "INCORRECT_BASIC_AUTH_FORMAT";
        assertThat(PaymentServerExceptionCode.isServerError(errorCode)).isTrue();
    }

}
