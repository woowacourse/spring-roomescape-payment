package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import fixture.PaymentFixture;
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

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentClient paymentClient;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentClient, paymentRepository);
    }

    @Test
    @DisplayName("결제 승인 - 성공")
    void confirmPayment_Success() {
        // given
        Payment payment = PaymentFixture.createDefault();
        given(paymentClient.requestPaymentConfirm(payment.getPaymentKey(), payment.getOrderId(), payment.getAmount()))
                .willReturn(new PaymentConfirmResponse(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                ));

        // when
        paymentService.confirmPayment(payment.getPaymentKey(), payment.getOrderId(), payment.getAmount());

        // then
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        Payment saved = captor.getValue();
        assertAll(
                () -> assertThat(saved.getPaymentKey()).isEqualTo(payment.getPaymentKey()),
                () -> assertThat(saved.getOrderId()).isEqualTo(payment.getOrderId()),
                () -> assertThat(saved.getAmount()).isEqualTo(payment.getAmount()),
                () -> assertThat(saved.getPaymentType()).isEqualTo(payment.getPaymentType())
        );
    }

    @Test
    @DisplayName("결제 승인 - 400 예외 실패")
    void confirmPayment_ThrowsBadRequestException() {
        // given
        Payment payment = PaymentFixture.createDefault();
        given(paymentClient.requestPaymentConfirm(payment.getPaymentKey(), payment.getOrderId(), payment.getAmount()))
                .willThrow(new BadRequestException("API 오류"));

        // when & then
        assertAll(
                () -> assertThatThrownBy(() -> paymentService.confirmPayment(
                        payment.getPaymentKey(), payment.getOrderId(), payment.getAmount()))
                        .isInstanceOf(BadRequestException.class),
                () -> verify(paymentRepository, never()).save(any())
        );
    }

    @Test
    @DisplayName("결제 승인 - 500 예외 실패")
    void confirmPayment_ThrowsInternalServerError() {
        // given
        Payment payment = PaymentFixture.createDefault();
        given(paymentClient.requestPaymentConfirm(payment.getPaymentKey(), payment.getOrderId(), payment.getAmount()))
                .willThrow(new ServerException("API 오류"));

        // when & then
        assertAll(
                () -> assertThatThrownBy(() -> paymentService.confirmPayment(
                        payment.getPaymentKey(), payment.getOrderId(), payment.getAmount()))
                        .isInstanceOf(ServerException.class),
                () -> verify(paymentRepository, never()).save(any())
        );
    }
}
