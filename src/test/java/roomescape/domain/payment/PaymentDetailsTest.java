package roomescape.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaymentDetailsTest {

    @Test
    @DisplayName("승인된 결제 세부사항을 생성한다.")
    void createWithConfirmation() {
        var paymentConfirmation = new PaymentConfirmation("a", "1", "order", 1000);

        var paymentDetails = new PaymentDetails(paymentConfirmation);

        assertAll(
            () -> assertThat(paymentDetails.confirmation()).isNotNull(),
            () -> assertThat(paymentDetails.isFailed()).isFalse()
        );
    }

    @Test
    @DisplayName("실패한 결제 세부사항을 생성한다.")
    void createWithStatus() {
        var paymentStatus = PaymentStatus.fail(PaymentStatusCode.FAILED_PAYMENT, "결제 실패");

        var paymentDetails = new PaymentDetails(paymentStatus);

        assertAll(
            () -> assertThat(paymentDetails.confirmation()).isNull(),
            () -> assertThat(paymentDetails.isFailed()).isTrue()
        );
    }
}
