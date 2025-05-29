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
    public TossRestClient tossRestClient(RestClient.Builder restClientBuilder, RestClientProperties restClientProperties) {
        return new TossRestClient(restClientBuilder
                .baseUrl(restClientProperties.getBaseUrl())
                .requestFactory(clientHttpRequestFactory())
                .build());
        // "https://api.tosspayments.com"
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        // 연결 최대 대기 시간 (밀리초)
        factory.setConnectTimeout(5_000);
        // 커넥션 풀에서 커넥션 가져올 최대 대기 시간 (밀리초)
        factory.setConnectionRequestTimeout(2_000);
        // 응답 읽기 최대 대기 시간 (밀리초)
        factory.setReadTimeout(5_000);
        return factory;
    }
}
