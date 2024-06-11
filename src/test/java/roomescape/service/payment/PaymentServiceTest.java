package roomescape.service.payment;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.Fixture;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.payment.PaymentType;
import roomescape.exception.PaymentException;
import roomescape.service.ServiceTestBase;
import roomescape.service.payment.dto.PaymentRequest;

class PaymentServiceTest extends ServiceTestBase {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;

    @DisplayName("결제 정보를 DB에 저장한다.")
    @ParameterizedTest
    @ValueSource(longs = {1000, 10000})
    void savePayment(long amount) {
        // given
        PaymentRequest paymentRequest = new PaymentRequest(
                Fixture.TEST_PAYMENT_KEY, Fixture.TEST_ORDER_ID,
                BigDecimal.valueOf(amount), PaymentType.NORMAL
        );

        // when
        Payment confirmed = paymentService.confirm(paymentRequest);

        // then
        long paymentId = confirmed.getId();
        long actual = paymentRepository.findById(paymentId).orElseThrow().getAmount().longValue();
        assertThat(actual).isEqualTo(amount);
    }

    @DisplayName("결제를 취소한다.")
    @Test
    void cancelPayment() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest(
                Fixture.TEST_PAYMENT_KEY, Fixture.TEST_ORDER_ID,
                BigDecimal.TEN, PaymentType.NORMAL
        );
        Payment saved = paymentService.confirm(paymentRequest);

        // when
        paymentService.cancel(saved);

        // then
        long paymentId = saved.getId();
        assertThat(paymentRepository.findById(paymentId)).isNotPresent();
    }

    @DisplayName("결제 취소 중 오류가 발생하면 결제정보가 삭제되지 않는다.")
    @Test
    void cannotCancelWhenExceptionThrown() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest(
                Fixture.CANCEL_ERROR_KEY, Fixture.TEST_ORDER_ID,
                BigDecimal.TEN, PaymentType.NORMAL
        );
        Payment saved = paymentService.confirm(paymentRequest);

        // when
        try {
            paymentService.cancel(saved);
        } catch (PaymentException ignored) {
        }

        // then
        assertThat(paymentRepository.findById(saved.getId())).isPresent();
    }
}
