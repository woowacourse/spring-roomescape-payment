package roomescape.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaymentFailureTest {

    @Test
    @DisplayName("결제 실패 정보를 생성한다.")
    void newPaymentFailure() {
        var succeed = new PaymentFailure(PaymentFailCode.CONDITION_NOT_SATISFIED, "결제 실패");

        assertAll(
            () -> assertThat(succeed.code()).isEqualTo(PaymentFailCode.CONDITION_NOT_SATISFIED),
            () -> assertThat(succeed.message()).isEqualTo("결제 실패")
        );
    }
}
