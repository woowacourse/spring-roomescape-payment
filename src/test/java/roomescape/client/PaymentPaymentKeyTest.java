package roomescape.client;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import roomescape.client.dto.request.TossPaymentConfirmRequest;
import roomescape.common.exception.PaymentException;

import org.junit.jupiter.api.Test;

@SpringBootTest
public class PaymentPaymentKeyTest {

    @Autowired
    private TossPaymentClient tossPaymentClient;

    @Test
    void 클라이언트에서_획득하지_않은_페이먼트_키로_요청_시_실패한다() {
        // given
        TossPaymentConfirmRequest tossPaymentConfirmRequest = new TossPaymentConfirmRequest(
                "orderId",
                1000L,
                "wrongPaymentKey"
        );

        // when & then
        PaymentException exception = assertThrows(PaymentException.class, () -> {
            tossPaymentClient.confirmPayment(tossPaymentConfirmRequest);
        });
        Assertions.assertThat(exception.getMessage())
                .isEqualTo("결제 실패 : 결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다.");
        Assertions.assertThat(exception.getStatusCode().toString()).isEqualTo("401 UNAUTHORIZED");
    }
}
