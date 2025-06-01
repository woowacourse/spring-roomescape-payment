package roomescape.payment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static roomescape.helper.TestFixture.PAYMENT;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.reservation.external.client.PaymentRestClient;
import roomescape.reservation.external.dto.respone.PaymentApproveResponse;
import roomescape.reservation.external.error.exception.PaymentClientException;
import roomescape.reservation.repository.PaymentRepository;
import roomescape.reservation.service.PaymentService;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRestClient paymentRestClient;

    @Mock
    private PaymentRepository paymentRepository;

    @DisplayName("예외가 발생하면, 결제 정보를 저장하지 않는다.")
    @Test
    void dontSavePaymentWhenThrowException() {
        // given
        when(paymentRestClient.approve(any()))
                .thenThrow(PaymentClientException.class);

        // when & then
        assertThatThrownBy(() -> paymentService.create(PAYMENT))
                .isInstanceOf(PaymentClientException.class);
    }

    @DisplayName("결제가 승인되면, 결제 정보를 저장한다.")
    @Test
    void savePayment() {
        // given
        PaymentApproveResponse paymentApproveResponse = new PaymentApproveResponse("paymentKey", "orderId");
        when(paymentRestClient.approve(any()))
                .thenReturn(paymentApproveResponse);
        when(paymentRepository.save(any()))
                .thenReturn(PAYMENT);

        // when
        paymentService.create(PAYMENT);

        // then
        verify(paymentRepository).save(any());
    }
}
