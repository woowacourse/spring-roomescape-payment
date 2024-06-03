package roomescape.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.client.payment.PaymentClient;

@Configuration
public class ClientConfig {

    private static final String TOSS_PAYMENT_CONFIRM_URL = "https://api.tosspayments.com";

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }

    @Bean
    public PaymentClient paymentClient(@Value("${payment.widget-secret-key}") String widgetSecretKey,
                                       RestClient restClient) {
        return new PaymentClient(widgetSecretKey, restClient, new ObjectMapper());
    }

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> restClientBuilder
                .requestFactory(getClientHttpRequestFactory())
                .baseUrl(TOSS_PAYMENT_CONFIRM_URL);
    }

    private SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
        return new SimpleClientHttpRequestFactory();
    }
}
