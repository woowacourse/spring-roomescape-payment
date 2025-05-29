package roomescape.payment.infrastructure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(RestClientProperties.class)
public class RestClientConfig {

    private static final int CONNECT_TIMEOUT = 5_000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 2_000;

    @Bean
    public TossRestClient tossRestClient(RestClient.Builder restClientBuilder, RestClientProperties restClientProperties) {
        return new TossRestClient(restClientBuilder
                .baseUrl(restClientProperties.getBaseUrl())
                .requestFactory(clientHttpRequestFactory())
                .build());
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        factory.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT);
        factory.setReadTimeout(5_000);
        return factory;
    }
}
