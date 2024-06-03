package roomescape.service.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TossPaymentErrorCodeUtilsTest {

    private final TossPaymentErrorCodeUtils errorCodeUtils = new TossPaymentErrorCodeUtils();

    @DisplayName("성공: 사용자에게 보여질 수 있는 에러 코드")
    @Test
    void isNotDisplayableErrorCode_False() {
        assertThat(errorCodeUtils.isNotDisplayableErrorCode("ALREADY_PROCESSED_PAYMENT")).isFalse();
    }

    @DisplayName("성공: 사용자에게 보여지면 안 되는 에러 코드")
    @Test
    void isNotDisplayableErrorCode_True() {
        assertThat(errorCodeUtils.isNotDisplayableErrorCode("INCORRECT_BASIC_AUTH_FORMAT")).isTrue();
    }

}
