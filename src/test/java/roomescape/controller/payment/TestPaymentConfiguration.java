package roomescape.controller.payment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.service.PaymentClient;
import roomescape.service.request.PaymentApproveDto;

@TestConfiguration
public class TestPaymentConfiguration {

    @MockBean
    private PaymentClient paymentClient;

    @PostConstruct
    private void initMock() {
        given(paymentClient.approve(any(PaymentApproveDto.class))).willReturn(null);
    }
}
