package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.global.error.exception.BadRequestException;
import roomescape.global.error.exception.ServerException;
import roomescape.payment.client.PaymentClient;
import roomescape.payment.dto.response.PaymentConfirmResponse;
import roomescape.payment.entity.Payment;
import roomescape.payment.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentClient paymentClient;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentClient, paymentRepository);
    }

    @Test
    @DisplayName("결제 승인 API를 호출한다.")
    void confirmPayment_Success() {
        // given
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 1000L;
        given(paymentClient.requestPaymentConfirm(paymentKey, orderId, amount))
                .willReturn(new PaymentConfirmResponse(paymentKey, orderId, amount, "NORMAL"));

        // when
        paymentService.confirmPayment(paymentKey, orderId, amount);

        // then
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        Payment saved = captor.getValue();
        assertAll(
                () -> assertThat(saved.getPaymentKey()).isEqualTo(paymentKey),
                () -> assertThat(saved.getOrderId()).isEqualTo(orderId),
                () -> assertThat(saved.getAmount()).isEqualTo(amount),
                () -> assertThat(saved.getPaymentType()).isEqualTo("NORMAL")
        );
    }

    @Test
    @DisplayName("결제 승인 API를 호출해서 400 예외가 터진다.")
    void confirmPayment_ThrowsBadRequestException() {
        // given
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 1000L;
        given(paymentClient.requestPaymentConfirm(paymentKey, orderId, amount))
                .willThrow(new BadRequestException("API 오류"));

        // when & then
        assertAll(
                () -> assertThatThrownBy(() -> paymentService.confirmPayment(paymentKey, orderId, amount))
                        .isInstanceOf(BadRequestException.class),
                () -> verify(paymentRepository, never()).save(any())
        );
    }

    @Test
    @DisplayName("결제 승인 API를 호출해서 500 예외가 터진다.")
    void confirmPayment_ThrowsInternalServerError() {
        // given
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 1000L;
        given(paymentClient.requestPaymentConfirm(paymentKey, orderId, amount))
                .willThrow(new ServerException("API 오류"));

        // when & then
        assertAll(
                () -> assertThatThrownBy(() -> paymentService.confirmPayment(paymentKey, orderId, amount))
                        .isInstanceOf(ServerException.class),
                () -> verify(paymentRepository, never()).save(any())
        );
    }
}
