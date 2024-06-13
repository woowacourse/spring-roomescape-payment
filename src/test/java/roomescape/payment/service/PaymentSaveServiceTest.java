package roomescape.payment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static roomescape.Fixture.JOJO_RESERVATION;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.PaymentRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
class PaymentSaveServiceTest {

    @SpyBean
    private PaymentService paymentService;

    @MockBean
    private PaymentRepository paymentRepository;

    @DisplayName("결제 저장 실패 시 재요청한다.")
    @Test
    void retrySaveWhenFailure() {
        Payment payment = new Payment("paymentKey", "orderId", 1000L, JOJO_RESERVATION);

        when(paymentRepository.save(payment)).thenThrow(IllegalArgumentException.class);

        Assertions.assertThatThrownBy(() -> paymentService.save(payment))
                .isInstanceOf(IllegalArgumentException.class);

        verify(paymentService, times(2)).save(any());
    }

    @DisplayName("결제 저장 성공 시 재요청 하지 않는다.")
    @Test
    void doesNotRetrySaveWhenSuccess() {
        Payment payment = new Payment("paymentKey", "orderId", 1000L, JOJO_RESERVATION);

        when(paymentRepository.save(payment)).thenReturn(payment);

        paymentService.save(payment);

        verify(paymentService, times(1)).save(any());
    }
}
