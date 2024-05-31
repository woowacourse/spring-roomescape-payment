package roomescape.controller.payment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.service.PaymentClient;
import roomescape.service.request.PaymentApproveDto;
import roomescape.service.response.PaymentApproveSuccessDto;

@TestConfiguration
public class TestPaymentConfiguration {

    @MockBean
    private PaymentClient paymentClient;

    @PostConstruct
    private void initMock() {
        given(paymentClient.approve(any(PaymentApproveDto.class))).willReturn(
                new PaymentApproveSuccessDto(
                        "paymentKey",
                        "orderId",
                        1000L,
                        "DONE",
                        "2022-06-01T00:00:00+09:00",
                        "2022-06-01T00:00:00+09:00"
                )
        );
    }
}
