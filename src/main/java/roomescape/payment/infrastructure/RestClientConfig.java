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

    @Bean
    public RestClient restClient(
            final RestClientProperties restClientProperties,
            final ClientHttpRequestFactory clientHttpRequestFactory
    ) {
        return RestClient.builder()
                .baseUrl(restClientProperties.getBaseUrl())
                .requestFactory(clientHttpRequestFactory)
                .build();
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(final RestClientProperties restClientProperties) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(restClientProperties.getConnectTimeout());
        factory.setConnectionRequestTimeout(restClientProperties.getConnectionRequestTimeout());
        factory.setReadTimeout(restClientProperties.getReadTimeout());
        return factory;
    }
}
