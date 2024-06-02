package roomescape.payment.client.toss;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.ClientException;
import roomescape.payment.client.PaymentClient;
import roomescape.payment.client.PaymentProperties;
import roomescape.payment.client.PaymentRestClientConfiguration;
import roomescape.payment.dto.request.ConfirmPaymentRequest;
import roomescape.payment.model.Payment;
import roomescape.util.IntegrationTest;

@IntegrationTest
class TossRestClientTest {

    private final RestClient restClient;

    @Autowired
    TossRestClientTest(final RestClient restClient) {
        this.restClient = restClient;
    }

    @Test
    @DisplayName("토스 결제 승인 실패: 유저에게 알리지 않을 사유라면 디폴트 메시지 전달")
    void confirm_WhenTossErrorCodeForUser() {
        ConfirmPaymentRequest confirmPaymentRequest = new ConfirmPaymentRequest("paymetKey", "orderId", 100L);

        assertThatThrownBy(
                () -> restClient.post()
                        .uri("/confirm")
                        .header("TossPayments-Test-Code", "UNAUTHORIZED_KEY")
                        .body(confirmPaymentRequest)
                        .retrieve()
                        .toEntity(Payment.class))
                .isInstanceOf(ClientException.class)
                .hasMessage("결제 오류입니다. 같은 문제가 반복된다면 문의해주세요.");
    }

    @Test
    @DisplayName("토스 결제 승인 실패: 유저에게 알릴 사유라면 토스 에러 메시지 전달")
    void confirm_WhenTossErrorCodeNotForUser() {
        ConfirmPaymentRequest confirmPaymentRequest = new ConfirmPaymentRequest("paymetKey", "orderId", 100L);

        assertThatThrownBy(
                () -> restClient.post()
                        .uri("/confirm")
                        .header("TossPayments-Test-Code", "EXCEED_MAX_ONE_DAY_AMOUNT")
                        .body(confirmPaymentRequest)
                        .retrieve()
                        .toEntity(Payment.class))
                .isInstanceOf(ClientException.class)
                .hasMessage("일일 한도를 초과했습니다.");
    }
}
