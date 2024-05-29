package roomescape.controller.payment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.infrastructure.payment.PaymentManager;

@TestConfiguration
public class TestPaymentConfiguration {

    @MockBean
    private PaymentManager paymentManager;

    @PostConstruct
    private void initMock() {
        doNothing().when(paymentManager).approve(any());
    }
}
