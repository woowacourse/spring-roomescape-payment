package roomescape.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import roomescape.domain.Payment;
import roomescape.infrastructure.repository.PaymentRepository;
import roomescape.infrastructure.thirdparty.PaymentRestClient;
import roomescape.infrastructure.thirdparty.dto.PaymentConfirmResponse;
import roomescape.infrastructure.thirdparty.exception.PaymentException;
import roomescape.presentation.dto.request.PaymentProcessRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRestClient paymentRestClient;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void 결제를_진행한다() {
        PaymentProcessRequest request = new PaymentProcessRequest("paymentKey", "orderId", "1000");
        Payment payment = Payment.create("paymentKey", "orderId");
        PaymentConfirmResponse response = new PaymentConfirmResponse("paymentKey", "orderId");

        when(paymentRestClient.getPaymentResponse(request)).thenReturn(response);
        when(paymentRepository.save(payment)).thenReturn(payment);

        Payment resultPayment = paymentService.processPayment(request);
        assertThat(resultPayment.getPaymentKey()).isEqualTo(payment.getPaymentKey());

        verify(paymentRestClient, times(1)).getPaymentResponse(request);
    }

    @Test
    void 결제가_실패하면_예외가_발생한다() {
        PaymentProcessRequest request = new PaymentProcessRequest("paymentKey", "orderId", "1000");

        when(paymentRestClient.getPaymentResponse(request))
                .thenThrow(new PaymentException("결제 실패", HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> paymentService.processPayment(request))
                .isInstanceOf(PaymentException.class);

        verify(paymentRestClient, times(1)).getPaymentResponse(request);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void 토스서버_내부에러가_발생하면_예외가_발생한다() {
        PaymentProcessRequest request = new PaymentProcessRequest("paymentKey", "orderId", "1000");

        when(paymentRestClient.getPaymentResponse(request))
                .thenThrow(new PaymentException("토스 서버 에러", HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> paymentService.processPayment(request))
                .isInstanceOf(PaymentException.class);

        verify(paymentRestClient, times(1)).getPaymentResponse(request);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void 결제_시스템과_통신이_불가할때_예외가_발생한다() {
        PaymentProcessRequest request = new PaymentProcessRequest("paymentKey", "orderId", "1000");

        when(paymentRestClient.getPaymentResponse(request))
                .thenThrow(new PaymentException("통신 오류", HttpStatus.SERVICE_UNAVAILABLE));

        assertThatThrownBy(() -> paymentService.processPayment(request))
                .isInstanceOf(PaymentException.class);

        verify(paymentRestClient, times(1)).getPaymentResponse(request);
        verify(paymentRepository, never()).save(any());
    }
}
