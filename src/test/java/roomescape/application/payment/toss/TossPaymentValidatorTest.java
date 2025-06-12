package roomescape.application.payment.toss;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import roomescape.application.payment.OrderAmountVerificationCache;
import roomescape.application.payment.toss.dto.TossPaymentValidationCommand;
import roomescape.infrastructure.error.exception.PaymentException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class TossPaymentValidatorTest {

    @Autowired
    private OrderAmountVerificationCache orderAmountVerificationCache;

    @Autowired
    private TossPaymentValidator tossPaymentValidator;

    @Test
    void 정상_주문번호과_금액_검증_테스트() {
        // given
        final TossPaymentValidationCommand command = new TossPaymentValidationCommand("orderId", 10000L);

        // when
        tossPaymentValidator.register(command);

        // then
        assertThatCode(() -> orderAmountVerificationCache.check(command.orderId(), command.amount()))
                .doesNotThrowAnyException();
    }

    @Test
    void 존재하지않는_주문번호로_검증하면_예외가_발생한다() {
        // given
        final TossPaymentValidationCommand command = new TossPaymentValidationCommand("orderId", 10000L);

        // when
        // then
        assertThatThrownBy(() -> orderAmountVerificationCache.check(command.orderId(), command.amount()))
                .isInstanceOf(PaymentException.class)
                .hasMessageContaining("존재하지 않는 결제 정보입니다");
    }

    @Test
    void 중복되는_주문번호에_동일한_금액으로_등록되면_예외가_발생하지_않는다() {
        // given
        final TossPaymentValidationCommand command = new TossPaymentValidationCommand("orderId", 10000L);

        // when
        tossPaymentValidator.register(command);

        // then
        assertThatCode(() -> tossPaymentValidator.register(command))
                .doesNotThrowAnyException();
    }

    @Test
    void 중복되는_주문번호에_다른_금액으로_등록되면_예외가_발생한() {
        // given
        final TossPaymentValidationCommand command1 = new TossPaymentValidationCommand("orderId", 10000L);
        final TossPaymentValidationCommand command2 = new TossPaymentValidationCommand("orderId", 20000L);

        // when
        tossPaymentValidator.register(command1);

        // then
        assertThatThrownBy(() -> tossPaymentValidator.register(command2))
                .isInstanceOf(PaymentException.class)
                .hasMessageContaining("이미 존재하는 주문 번호에 다른 금액으로 등록을 시도할 수 없습니다.");

    }
}
