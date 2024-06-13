package roomescape.payment.client;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.client.toss.TossPaymentErrorHandler;

@Configuration
public class PaymentRestClientConfiguration {

    private static final String BASIC_PREFIX = "Basic ";

    private final PaymentProperties paymentProperties;

    public PaymentRestClientConfiguration(final PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
    }

    @Bean
    public RestClient tossRestClient() {
        return restClient()
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .defaultHeader(HttpHeaders.AUTHORIZATION, getClientAuthorizationValue())
                .defaultStatusHandler(new TossPaymentErrorHandler())
                .build();
    }

    @Bean
    public RestClient.Builder restClient() {
        return RestClient.builder()
                .requestFactory(getClientHttpRequestFactory());
    }

    private String getClientAuthorizationValue() {
        byte[] encodedBytes = Base64.getEncoder()
                .encode((paymentProperties.getSecretKey() + paymentProperties.getPassword())
                        .getBytes(StandardCharsets.UTF_8));

        return BASIC_PREFIX + new String(encodedBytes);
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(3);
        simpleClientHttpRequestFactory.setReadTimeout(30);
        return simpleClientHttpRequestFactory;
    }
}
