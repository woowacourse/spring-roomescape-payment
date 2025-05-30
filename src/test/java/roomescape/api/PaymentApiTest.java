package roomescape.api;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import roomescape.exception.PaymentException;
import roomescape.utility.PaymentClient;
import roomescape.utility.PaymentClientStub;
import roomescape.utility.TossPaymentClient;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class PaymentApiTest {

    private final String secretKey;
    private final String paymentUrl;
    private final TossPaymentClient realPaymentClient;
    private final String confirmServerUrl;

    public PaymentApiTest(
            @Value("${toss_payment_base_url}") String paymentUrl,
            @Value("${toss_payment_secret_key}") String secretKey,
            @Value("${toss_confirm_server_uri}") String confirmServerUri
    ) {
        this.paymentUrl = paymentUrl;
        this.secretKey = secretKey;
        this.confirmServerUrl = confirmServerUri;
        RestClient realRestClient = RestClient.builder().baseUrl(paymentUrl).build();
        realPaymentClient = new TossPaymentClient(realRestClient, secretKey, confirmServerUri);
    }

    @Test
    void ifNotValidUrlThenError() {
        RestClient invalidRestClient = RestClient.builder().baseUrl(paymentUrl + "asdf").build();
        PaymentClient invalidPaymentClient = new TossPaymentClient(invalidRestClient, secretKey, confirmServerUrl);
        assertThatThrownBy(() -> invalidPaymentClient.pay("asdf", "asdf", 1234))
                .isInstanceOf(RestClientException.class);
    }

    @Test
    void ifTimeoutThenError() {
        PaymentClientStub invalidPaymentClient = new PaymentClientStub();
        invalidPaymentClient.setOccurRestClientError(true);
        assertThatThrownBy(() -> invalidPaymentClient.pay("asdf", "asdf", 1234))
                .isInstanceOf(RestClientException.class);
    }

    @Test
    void connectSuccess() {
        assertThatThrownBy(() -> realPaymentClient.pay("asdf", "asdf", 1234))
                .isNotInstanceOf(RestClientException.class);
    }

    @Test
    void notFoundWhenNotPreparePayment() {
        assertThatThrownBy(() -> realPaymentClient.pay("asdf", "asdf", 1234))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다.");
    }
}
