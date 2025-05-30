package roomescape.payment.client;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import roomescape.common.exception.ClientPaymentException;
import roomescape.payment.client.dto.request.TossPaymentConfirmRequest;

@SpringBootTest
@Import(TossPaymentTestConfig.class)
class PaymentSecretKeyClientTest {

    @Autowired
    TossPaymentClient tossPaymentClient;

    @Test
    void 시크릿키를_정상적이지_않은_값으로_요청_시_실패한다() {
        // given
        TossPaymentConfirmRequest tossPaymentConfirmRequest = new TossPaymentConfirmRequest(
                "orderId",
                1000L,
                "paymentKey"
        );

        Assertions.assertThatThrownBy(() -> tossPaymentClient.confirmPayment(tossPaymentConfirmRequest))
                .isInstanceOf(ClientPaymentException.class)
                .hasMessage("결제 실패 : 인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다.");
    }
}
