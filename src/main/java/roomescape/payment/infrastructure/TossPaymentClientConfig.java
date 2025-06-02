package roomescape.payment.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(TossPaymentProperties.class)
public class TossPaymentClientConfig {

    private final TossPaymentProperties tossPaymentProperties;

    @Bean
    public TossRestClient tossRestClient(final TossPaymentProperties tossPaymentProperties) {
        RestClient restClient = RestClient.builder()
                .requestFactory(clientHttpRequestFactory())
                .baseUrl(tossPaymentProperties.getBaseUrl())
                .build();

        return new TossRestClient(restClient, tossPaymentProperties);
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(tossPaymentProperties.getConnectTimeout());
        factory.setConnectionRequestTimeout(tossPaymentProperties.getConnectionRequestTimeout());
        factory.setReadTimeout(tossPaymentProperties.getReadTimeout());
        return factory;
    }
}
