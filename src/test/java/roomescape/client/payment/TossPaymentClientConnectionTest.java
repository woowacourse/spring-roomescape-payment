package roomescape.client.payment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentException;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
class TossPaymentClientConnectionTest {

    @Value("${toss_payment_base_url}")
    private String paymentUrl;
    @Value("${toss_payment_secret_key}")
    private String secretKey;
    @Value("${toss_payment_authorization_url}")
    private String paymentAuthorizationUrl;

    @DisplayName("실제 토스 결제 시스템에 연결할 수 있다.")
    @Test
    void canCheckConnection() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(30));

        RestClient restClient = RestClient.builder()
                .baseUrl(paymentUrl)
                .defaultHeaders(headers -> headers.set("Content-Type", "application/json"))
                .build();

        TossPaymentClient tossPaymentClient = new TossPaymentClient(restClient, secretKey, paymentAuthorizationUrl);

        // when & then
        assertThatThrownBy(() -> tossPaymentClient.authorizePayment("payment_key", "order_id", 1000L))
                .isInstanceOf(PaymentException.class);
    }
}
