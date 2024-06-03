package roomescape.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import roomescape.exception.PaymentException;
import roomescape.payment.config.PaymentClient;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.service.PaymentService;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private PaymentService paymentService;

    @DisplayName("적합한 인자를 통한 결제 요청 시 성공한다.")
    @Test
    void payment() {
        PaymentRequest validPaymentRequest = new PaymentRequest("valid", "validOrderId", 1000);
        PaymentResponse validPaymentResponse = new PaymentResponse(
                "validOrderName",
                "requestedAt",
                "approvedAt",
                "KRW",
                1000);

        Mockito.when(paymentClient.payment(validPaymentRequest)).thenReturn(validPaymentResponse);

        assertThat(paymentService.payment(validPaymentRequest)).isEqualTo(validPaymentResponse);
    }

    @DisplayName("적합하지 못한 인자를 통한 결제 요청 시 실패한다.")
    @Test
    void failPayment() {
        PaymentRequest invalidPaymentRequest = new PaymentRequest("invalid", "invalidOrderId", 1000);

        Mockito.when(paymentClient.payment(invalidPaymentRequest)).thenThrow(PaymentException.class);

        assertThatThrownBy(() -> paymentService.payment(invalidPaymentRequest)).isInstanceOf(PaymentException.class);
    }
}
