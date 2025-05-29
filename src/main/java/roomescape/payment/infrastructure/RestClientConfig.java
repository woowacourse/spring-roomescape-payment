package roomescape.payment.infrastructure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient.Builder;

@Configuration
@EnableConfigurationProperties(RestClientProperties.class)
public class RestClientConfig {

    private int connectTimeout;
    private int connectionRequestTimeout;
    private int readTimeout;

    @Bean
    public TossRestClient tossRestClient(final Builder restClientBuilder, final RestClientProperties restClientProperties) {
        connectTimeout = restClientProperties.getConnectTimeout();
        connectionRequestTimeout = restClientProperties.getConnectionRequestTimeout();
        readTimeout = restClientProperties.getReadTimeout();
        return new TossRestClient(restClientBuilder
                .baseUrl(restClientProperties.getBaseUrl())
                .requestFactory(clientHttpRequestFactory())
                .build());
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setConnectionRequestTimeout(connectionRequestTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }
}
