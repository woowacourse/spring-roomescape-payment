package roomescape.api;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import roomescape.exception.ExternalApiConnectionException;
import roomescape.exception.PaymentException;
import roomescape.test.stub.PaymentClientStub;
import roomescape.utility.payment.PaymentClient;
import roomescape.utility.payment.TossPaymentClient;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class PaymentApiTest {

    private final String secretKey;
    private final String paymentUrl;
    private final TossPaymentClient realPaymentClient;
    private final String confirmServerUrl;

    public PaymentApiTest(
            @Value("${toss_payment_base_url}") String paymentUrl,
            @Value("${toss_payment_secret_key}") String secretKey,
            @Value("${toss_payment_authorization_url}") String confirmServerUrl
    ) {
        this.paymentUrl = paymentUrl;
        this.secretKey = secretKey;
        this.confirmServerUrl = confirmServerUrl;
        RestClient realRestClient = RestClient.builder().baseUrl(paymentUrl).build();
        realPaymentClient = new TossPaymentClient(realRestClient, secretKey, confirmServerUrl);
    }

    @Test
    void ifNotValidUrlThenError() {
        RestClient invalidRestClient = RestClient.builder().baseUrl(paymentUrl + "asdf").build();
        PaymentClient invalidPaymentClient = new TossPaymentClient(invalidRestClient, secretKey, confirmServerUrl);
        assertThatThrownBy(() -> invalidPaymentClient.authorizePayment("asdf", "asdf", 1234))
                .isInstanceOf(ExternalApiConnectionException.class);
    }

    @Test
    void ifTimeoutThenError() {
        PaymentClientStub invalidPaymentClient = new PaymentClientStub();
        invalidPaymentClient.setOccurRestClientError(true);
        assertThatThrownBy(() -> invalidPaymentClient.authorizePayment("asdf", "asdf", 1234))
                .isInstanceOf(RestClientException.class);
    }

    @Test
    void connectSuccess() {
        assertThatThrownBy(() -> realPaymentClient.authorizePayment("asdf", "asdf", 1234))
                .isNotInstanceOf(RestClientException.class);
    }

    @Test
    void notFoundWhenNotPreparePayment() {
        assertThatThrownBy(() -> realPaymentClient.authorizePayment("asdf", "asdf", 1234))
                .isInstanceOf(PaymentException.class)
                .hasMessage("결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다.");
    }
}
