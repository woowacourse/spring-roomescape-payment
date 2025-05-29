package roomescape.domain.payment;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;
import roomescape.infrastructure.error.exception.PaymentException;

class PaymentTest {

    @Test
    void 승인_요청_금액과_결제_금액이_일치하지_않으면_예외가_발생한다() {
        // given
        String orderId = "orderId";
        long amount = 10_000L;
        Payment payment = new Payment(orderId, amount);
        long approvalAmount = 20_000L;

        // when
        // then
        assertThatCode(() -> payment.validateApprovalAmount(approvalAmount))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 금액(10,000)과 승인 요청 금액(10,000)이 일치하지 않아 결제 승인을 거부합니다.");
    }

    @Test
    void 승인_요청_금액과_결제_금액이_일치하면_예외가_발생하지_않는다() {
        // given
        String orderId = "orderId";
        long amount = 10_000L;
        Payment payment = new Payment(orderId, amount);

        // when
        // then
        assertThatCode(() -> payment.validateApprovalAmount(10_000L))
                .doesNotThrowAnyException();
    }
}
