package roomescape.controller.payment;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.infrastructure.payment.PaymentManager;
import roomescape.service.request.PaymentApproveDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestConfiguration
public class TestPaymentConfiguration {

    @MockBean
    private PaymentManager paymentManager;

    @PostConstruct
    private void initMock()
    {
        when(paymentManager.approve(any()))
                .thenReturn(new PaymentApproveDto("paymentKey", "orderId", 1000L));
    }
}
