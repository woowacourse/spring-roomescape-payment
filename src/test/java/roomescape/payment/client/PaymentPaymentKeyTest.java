package roomescape.payment.client;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import roomescape.common.exception.PaymentException;
import roomescape.payment.client.dto.request.TossPaymentConfirmRequest;

@SpringBootTest
public class PaymentPaymentKeyTest {

    @Autowired
    TossPaymentClient tossPaymentClient;

    @Test
    void 클라이언트에서_획득하지_않은_페이먼트_키로_요청_시_실패한다() {
        // given
        TossPaymentConfirmRequest tossPaymentConfirmRequest = new TossPaymentConfirmRequest(
                "orderId",
                1000L,
                ""
        );

        Assertions.assertThatThrownBy(() -> tossPaymentClient.confirmPayment(tossPaymentConfirmRequest))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 실패 : 결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다.");
    }
}
