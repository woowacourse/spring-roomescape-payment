package roomescape.reservation.payment.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class InternalServerErrorCodeTest {

    @DisplayName("에러 코드가 서버 에러 코드인지 확인한다.")
    @CsvSource(value = {
            "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING,true",
            "FAILED_INTERNAL_SYSTEM_PROCESSING,true",
            "UNKNOWN_PAYMENT_ERROR,true",
            "UNAUTHORIZED_KEY,true",
            "INCORRECT_BASIC_AUTH_FORMAT,true",
            "INVALID_API_KEY,true",
            "ALREADY_PROCESSED_PAYMENT,false"
    })
    @ParameterizedTest
    void test(String errorCode, boolean expected) {
        assertThat(InternalServerErrorCode.contains(errorCode)).isEqualTo(expected);
    }
}
