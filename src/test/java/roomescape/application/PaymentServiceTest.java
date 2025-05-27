package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.application.dto.PaymentProcessRequest;
import roomescape.config.PaymentRestClient;
import roomescape.domain.Payment;
import roomescape.infrastructure.repository.PaymentRepository;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRestClient paymentRestClient;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void 결제를_진행한다() {
        PaymentProcessRequest paymentProcessRequest = new PaymentProcessRequest("test", "test", "1000");
        Payment payment = new Payment(null, "test", "test");
        Payment savedPayment = new Payment(1L, "test", "test");

        when(paymentRestClient.getPayment(paymentProcessRequest)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(savedPayment);

        Payment resultPayment = paymentService.process(paymentProcessRequest);
        assertThat(resultPayment).isEqualTo(savedPayment);
        verify(paymentRestClient, times(1)).getPayment(paymentProcessRequest);
    }
}
