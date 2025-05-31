package roomescape.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaymentStatusTest {

    @Test
    @DisplayName("실패를 나타내는 결제 상태를 생성한다.")
    void fail() {
        var succeed = PaymentStatus.fail(PaymentFailCode.CONDITION_NOT_SATISFIED, "결제 실패");

        assertAll(
            () -> assertThat(succeed.code()).isEqualTo(PaymentFailCode.CONDITION_NOT_SATISFIED),
            () -> assertThat(succeed.message()).isEqualTo("결제 실패")
        );
    }
}
