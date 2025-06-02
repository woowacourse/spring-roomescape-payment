package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${api.toss.url}")
    private String tossPaymentBaseUrl;

    @Value("${api.toss.secret-key}")
    private String secretKey;

    @Bean
    public RestClient tossPaymentRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(15_000);

        return RestClient.builder()
                .baseUrl(tossPaymentBaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, AuthHeaderEncoder.BASIC.getHeaderValue(secretKey))
                .requestFactory(requestFactory)
                .build();
    }
}
