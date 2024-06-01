package roomescape.paymenthistory.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.paymenthistory.exception.PaymentException.PaymentServerError;

class SecretKeyTest {

    @DisplayName("SecretKey가 null 일 경우 예외를 던진다.")
    @Test
    void saveSecretKey_whenSecretKeyIsNull() {
        assertThatThrownBy(() -> new SecretKey(null))
                .isInstanceOf(PaymentServerError.class)
                .hasMessage("내부 서버 에러가 발생했습니다. 관리자에게 문의해주세요.");
    }
}
