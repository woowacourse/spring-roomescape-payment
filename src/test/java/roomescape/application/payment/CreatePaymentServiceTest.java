package roomescape.application.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import roomescape.application.payment.toss.dto.TossPaymentValidationCommand;
import roomescape.application.payment.toss.TossPaymentValidator;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
class TossPaymentValidatorTest {

    @Autowired
    private OrderAmountVerificationCache orderAmountVerificationCache;

    @Autowired
    private TossPaymentValidator tossPaymentValidator;

    @Test
    void 결제_생성_서비스_테스트() {
        // given
        final TossPaymentValidationCommand command = new TossPaymentValidationCommand("orderId", 10000L);

        // when
        tossPaymentValidator.register(command);

        // then
        assertThatCode(() -> orderAmountVerificationCache.check(command.orderId(), command.amount()))
                .doesNotThrowAnyException();
    }
}
