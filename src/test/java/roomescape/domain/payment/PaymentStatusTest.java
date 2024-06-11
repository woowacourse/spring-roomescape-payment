package roomescape.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;

class PaymentStatusTest {

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, mode = Mode.EXCLUDE, names = "PENDING")
    @DisplayName("결제는 결제 대기 상태에서만 가능하다.")
    void purchaseFail(PaymentStatus status) {
        assertThatCode(status::purchase)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("결제 대기 상태에서만 결제할 수 있습니다.");
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, mode = Mode.EXCLUDE, names = "SUCCESS")
    @DisplayName("결제 취소는 결제 완료 상태에서만 가능하다.")
    void cancelFail(PaymentStatus status) {
        assertThatCode(status::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("결제 성공 상태에서만 취소할 수 있습니다.");
    }

    @Test
    @DisplayName("결제 대기 상태에서 결제를 완료하면, 결제 완료 상태로 변경된다.")
    void purchaseSuccess() {
        PaymentStatus status = PaymentStatus.PENDING;
        assertThat(status.purchase()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    @DisplayName("결제 완료 상태에서 결제를 취소하면, 결제 취소 상태로 변경된다.")
    void cancelSuccess() {
        PaymentStatus status = PaymentStatus.SUCCESS;
        assertThat(status.cancel()).isEqualTo(PaymentStatus.CANCELLED);
    }
}
