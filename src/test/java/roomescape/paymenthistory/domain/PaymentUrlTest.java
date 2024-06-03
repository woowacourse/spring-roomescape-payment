package roomescape.paymenthistory.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.paymenthistory.exception.PaymentException.PaymentServerError;

class PaymentUrlTest {

    @DisplayName("PaymentUrl이 null일 경우 예외를 발생한다.")
    @Test
    void savePaymentUrl_whenPaymentUrlIsNull() {
        assertThatThrownBy(() -> new PaymentUrl(null))
                .isInstanceOf(PaymentServerError.class)
                .hasMessage("내부 서버 에러가 발생했습니다. 관리자에게 문의해주세요.");
    }
}
