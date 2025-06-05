package roomescape.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TransactionStatusTest {

    @Test
    @DisplayName("성공을 나타내는 결제 상태를 생성한다.")
    void succeed() {
        var succeed = TransactionStatus.succeed();

        assertThat(succeed.code()).isEqualTo(TransactionStatusCode.SUCCEEDED_PAYMENT);
    }

    @Test
    @DisplayName("실패를 나타내는 결제 상태를 생성한다.")
    void fail() {
        var succeed = TransactionStatus.fail(TransactionStatusCode.FAILED_PAYMENT, "결제 실패");

        assertAll(
            () -> assertThat(succeed.code()).isEqualTo(TransactionStatusCode.FAILED_PAYMENT),
            () -> assertThat(succeed.message()).isEqualTo("결제 실패")
        );
    }

    @Test
    @DisplayName("결제 상태가 실패인지 알 수 있다.")
    void isFailed() {
        var succeed = TransactionStatus.fail(TransactionStatusCode.FAILED_PAYMENT, "결제 실패");

        assertThat(succeed.isFailed()).isTrue();
    }
}
