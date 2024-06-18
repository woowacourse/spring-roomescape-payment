package roomescape.service.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TossPaymentDisplayableErrorCodeTest {

    @DisplayName("유저에게 표시 가능 에러 코드: ALREADY_PROCESSED_PAYMENT")
    @Test
    void from_Displayable() {
        assertThat(TossPaymentDisplayableErrorCode.from("ALREADY_PROCESSED_PAYMENT"))
            .isEqualTo(TossPaymentDisplayableErrorCode.ALREADY_PROCESSED_PAYMENT);
    }

    @DisplayName("유저에게 표시 불가능 에러 코드: 정의되지 않은 문자열")
    @Test
    void from_NotDisplayable() {
        assertThat(TossPaymentDisplayableErrorCode.from("RANDOM_ERROR_CODE"))
            .isEqualTo(TossPaymentDisplayableErrorCode.NOT_DISPLAYABLE_ERROR);
    }
}
