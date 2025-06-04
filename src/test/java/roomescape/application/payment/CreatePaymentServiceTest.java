package roomescape.application.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import roomescape.application.payment.dto.PaymentValidationCommand;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
class PaymentValidatorTest {

    @Autowired
    private OrderAmountVerificationCache orderAmountVerificationCache;

    @Autowired
    private PaymentValidator paymentValidator;

    @Test
    void 결제_생성_서비스_테스트() {
        // given
        final PaymentValidationCommand command = new PaymentValidationCommand("orderId", 10000L);

        // when
        paymentValidator.register(command);

        // then
        assertThatCode(() -> orderAmountVerificationCache.check(command.orderId(), command.amount()))
                .doesNotThrowAnyException();
    }
}
