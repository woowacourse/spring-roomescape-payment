package roomescape.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaymentStatusTest {

    @Test
    @DisplayName("실패를 나타내는 결제 상태를 생성한다.")
    void fail() {
        var succeed = PaymentStatus.fail(PaymentStatusCode.FAILED_PAYMENT, "결제 실패");

        assertAll(
            () -> assertThat(succeed.code()).isEqualTo(PaymentStatusCode.FAILED_PAYMENT),
            () -> assertThat(succeed.message()).isEqualTo("결제 실패")
        );
    }
}
