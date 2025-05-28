package roomescape.payment.processor.toss;

import java.util.Base64;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import static org.mockito.Mockito.when;

@SpringBootTest
class TossPaymentProcessorTest {

    private static final String SECRET_KEY = "SecretKey";
    private static final String ENCODED_SECRET_KEY = Base64.getEncoder()
            .encodeToString((SECRET_KEY + ":base64").getBytes());

    @Autowired
    @Qualifier("testTossPaymentProcessor")
    private TossPaymentProcessor tossPaymentProcessor;

    @Autowired
    private RestClient restClient;

    @Test
    void 토스_결제_요청에_따른_반환_확인() {
        // given
        final TossPaymentConfirmRequest request = new TossPaymentConfirmRequest(
                10000,
                "orderId",
                "paymentKey"
        );
        final TossPaymentConfirmResponse expected = new TossPaymentConfirmResponse(
                "orderId",
                "paymentKey"
        );
        final String uri = "https://api.tosspayments.com/v1/payments/confirm";

        when(restClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + ENCODED_SECRET_KEY)
                .body(request)
                .retrieve()
                .body(TossPaymentConfirmResponse.class)
        ).thenReturn(expected);

        // when
        final TossPaymentConfirmResponse actual = tossPaymentProcessor.processPayment(request);

        // then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @TestConfiguration
    static class TestTossPaymentConfig {

        @Bean
        public RestClient testRestClient() {
            return Mockito.mock(RestClient.class, Mockito.RETURNS_DEEP_STUBS);
        }

        @Bean(name = "testTossPaymentProcessor")
        public TossPaymentProcessor testTossPaymentProcessor(final RestClient testRestClient) {
            return new TossPaymentProcessor(SECRET_KEY, testRestClient);
        }
    }
}
