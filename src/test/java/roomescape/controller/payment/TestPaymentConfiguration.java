package roomescape.controller.payment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.service.PaymentClient;
import roomescape.service.request.PaymentApproveAppRequest;
import roomescape.service.response.PaymentApproveSuccessAppResponse;

@TestConfiguration
public class TestPaymentConfiguration {

    @MockBean
    private PaymentClient paymentClient;

    @PostConstruct
    private void initMock() {
        given(paymentClient.approve(any(PaymentApproveAppRequest.class))).willReturn(
                new PaymentApproveSuccessAppResponse(
                        "paymentKey",
                        "orderId",
                        BigDecimal.valueOf(1000),
                        "DONE",
                        "2022-06-01T00:00:00+09:00",
                        "2022-06-01T00:00:00+09:00"
                )
        );
    }
}
