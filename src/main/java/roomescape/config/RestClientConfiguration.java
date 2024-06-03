package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.domain.PaymentRestClient;

@Configuration
public class RestClientConfiguration {

    private static final String TOSS_PAYMENT_URL = "https://api.tosspayments.com";

    @Value("${payment.secret-key}")
    private String secretKey;

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return (restClientBuilder) -> restClientBuilder
                .requestFactory(getClientHttpRequestFactory())
                .baseUrl(TOSS_PAYMENT_URL);
    }

    private SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(1000);
        simpleClientHttpRequestFactory.setReadTimeout(5000);
        return simpleClientHttpRequestFactory;
    }

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }

    @Bean
    public PaymentRestClient paymentRestClient(RestClient restClient) {
        return new PaymentRestClient(restClient, secretKey);
    }
}
