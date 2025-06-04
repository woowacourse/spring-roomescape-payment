package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.common.AuthHeaderEncoder;

@Configuration
public class PaymentRestClientConfig {

    @Value("${api.toss.url}")
    private String tossPaymentBaseUrl;

    @Value("${api.toss.secret-key}")
    private String secretKey;

    @Value("${api.toss.timeout.connection}")
    private int connectionTimeout;

    @Value("${api.toss.timeout.read}")
    private int readTimeout;

    @Bean
    public RestClient tossPaymentRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectionTimeout);
        requestFactory.setReadTimeout(readTimeout);

        return RestClient.builder()
                .baseUrl(tossPaymentBaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, AuthHeaderEncoder.BASIC.getHeaderValue(secretKey))
                .requestFactory(requestFactory)
                .build();
    }
}
