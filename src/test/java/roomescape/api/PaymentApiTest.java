package roomescape.api;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentException;
import roomescape.utility.PaymentClient;
import roomescape.utility.TossPaymentClient;

@SpringBootTest
class PaymentApiTest {

    private final String secretKey;
    private final String paymentUrl;
    private final TossPaymentClient realPaymentClient;

    public PaymentApiTest(
            @Value("${toss_payment_url}") String paymentUrl,
            @Value("${toss_payment_secret_key}") String secretKey
    ) {
        this.paymentUrl = paymentUrl;
        this.secretKey = secretKey;
        RestClient realRestClient = RestClient.builder().baseUrl(paymentUrl).build();
        realPaymentClient = new TossPaymentClient(realRestClient, secretKey);
    }

    @Test
    void connectionError() {
        RestClient invalidRestClient = RestClient.builder().baseUrl(paymentUrl + "asdf").build();
        PaymentClient invalidPaymentClient = new TossPaymentClient(invalidRestClient, secretKey);
        assertThatThrownBy(() -> invalidPaymentClient.pay("asdf", "asdf", 1234))
                .isInstanceOf(ResourceAccessException.class);
    }

    @Test
    void connectSuccess() {
        assertThatThrownBy(() -> realPaymentClient.pay("asdf", "asdf", 1234))
                .isNotInstanceOf(ResourceAccessException.class);
    }

    @Test
    void notFoundWhenNotPreparePayment() {
        assertThatThrownBy(() -> realPaymentClient.pay("asdf", "asdf", 1234))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다.");
    }
}
