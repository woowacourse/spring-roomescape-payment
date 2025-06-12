package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.application.request.PaymentInfo;
import roomescape.application.response.PaymentResponse;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.exception.PaymentException;
import roomescape.infrastructure.payment.PaymentClient;
import roomescape.infrastructure.payment.PaymentErrorCode;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("결제 승인 요청이 성공하면 결제 정보를 저장하고 반환한다")
    void savePayment_Success() {
        // given
        PaymentInfo paymentInfo = new PaymentInfo("test_payment_key", "test_order_id", 1000);
        Payment payment = Payment.register(
                "test_order_id",
                "test_payment_key",
                "테스트 결제",
                1000
        );

        PaymentResponse paymentResponse = new PaymentResponse(
                "test_payment_key",
                "test_order_id",
                "테스트 결제",
                1000
        );

        when(paymentClient.confirmPayment(paymentInfo)).thenReturn(paymentResponse);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // when
        Payment savedPayment = paymentService.savePayment(paymentInfo);

        // then
        assertThat(savedPayment).isEqualTo(payment);
    }

    @Test
    @DisplayName("결제 승인 요청이 실패하면 PaymentException이 발생한다")
    void savePayment_Failure() {
        // given
        PaymentInfo paymentInfo = new PaymentInfo("test_payment_key", "test_order_id", 1000);
        PaymentException expectedException = new PaymentException(PaymentErrorCode.REJECT_CARD_PAYMENT);

        when(paymentClient.confirmPayment(paymentInfo)).thenThrow(expectedException);

        // when & then
        assertThatThrownBy(() -> paymentService.savePayment(paymentInfo))
                .isInstanceOf(PaymentException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.REJECT_CARD_PAYMENT)
                .hasMessageContaining("한도초과 혹은 잔액부족");
    }
} 
