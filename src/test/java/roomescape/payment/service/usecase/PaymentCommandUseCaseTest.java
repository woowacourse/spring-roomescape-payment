package roomescape.payment.service.usecase;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.common.exception.AlreadyExistException;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.FakePaymentRepository;
import roomescape.payment.repository.PaymentRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentCommandUseCaseTest {

    private PaymentRepository paymentRepository;
    private PaymentCommandUseCase paymentCommandUseCase;

    @BeforeEach
    void setUp() {
        paymentRepository = new FakePaymentRepository();
        paymentCommandUseCase = new PaymentCommandUseCase(paymentRepository);
    }

    @Test
    @DisplayName("결제 내역이 정상적으로 생성된다")
    void createPayment() {
        // given
        final Payment payment = Payment.withoutId(
                "paymentKey123",
                "orderId123",
                10000L,
                "123",
                "123"
        );

        // when
        final Payment actual = paymentCommandUseCase.create(payment);

        // then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getPaymentKey()).isEqualTo(payment.getPaymentKey());
    }

    @Test
    @DisplayName("paymentKey가 중복된 결제 내역이 생성되지 않는다")
    void canNotCreatePaymentIfPaymentKeyIsNotUnique() {
        // given
        final Payment payment = Payment.withoutId(
                "paymentKey123",
                "orderId123",
                10000L,
                "123",
                "123"
        );

        final Payment payment2 = Payment.withoutId(
                "paymentKey123",
                "orderId1234",
                10000L,
                "1234",
                "1234"
        );

        final Payment saved = paymentCommandUseCase.create(payment);

        // when & then
        assertThatThrownBy(() -> paymentCommandUseCase.create(payment2))
                .isInstanceOf(AlreadyExistException.class)
                .hasMessage("paymentKey에 대한 결제 내역이 이미 존재합니다.");
    }
}
