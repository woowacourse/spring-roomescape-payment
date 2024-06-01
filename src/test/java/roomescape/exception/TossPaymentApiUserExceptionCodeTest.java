package roomescape.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TossPaymentApiUserExceptionCodeTest {

    @Test
    @DisplayName("사용자에게 내려주지 않는 예외 코드이면 false를 반환하는지 확인")
    void t1() {
        Assertions.assertThat(TossPaymentApiUserExceptionCode.hasErrorCode("NON_CODE"))
                .isFalse();
    }

    @Test
    @DisplayName("사용자에게 내려주는 예외 코드이면 true를 반환하는지 확인")
    void t2() {
        Assertions.assertThat(TossPaymentApiUserExceptionCode.hasErrorCode("NOT_ALLOWED_POINT_USE"))
                .isTrue();
    }
}
