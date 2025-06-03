package roomescape.infrastructure.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.infrastructure.payment.toss.TossPaymentClient;

@Configuration
@EnableConfigurationProperties(value = TossPaymentProperties.class)
public class PaymentConfig {

    @Bean
    public TossPaymentClient tossPaymentClient(
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            TossPaymentProperties properties
    ) {
        RestClient tossRestClient = createTossRestClient(restClientBuilder, properties);
        return new TossPaymentClient(tossRestClient, objectMapper, properties.getSecretKey());
    }

    private RestClient createTossRestClient(RestClient.Builder restClientBuilder, TossPaymentProperties properties) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getTimeout().getConnect());
        factory.setReadTimeout(properties.getTimeout().getRead());
        return restClientBuilder
                .baseUrl(properties.getBaseUrl())
                .requestFactory(factory)
                .build();
    }
}
