package roomescape.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.domain.Payment;
import roomescape.infrastructure.repository.PaymentRepository;
import roomescape.infrastructure.thirdparty.PaymentRestClient;
import roomescape.infrastructure.thirdparty.dto.PaymentConfirmResponse;
import roomescape.presentation.dto.request.PaymentProcessRequest;

import static org.assertj.core.api.Assertions.assertThat;
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
}
