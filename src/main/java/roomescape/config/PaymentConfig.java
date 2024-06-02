package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
@EnableRetry
public class PaymentConfig {

    @Value("${payment.api.baseurl}")
    private String baseUrl;

    @Bean
    public RestClient paymentRestClient() {
        return RestClient.builder()
                        .baseUrl(baseUrl)
                        .requestFactory(clientHttpRequestFactory())
                        .build();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(30L));
        requestFactory.setReadTimeout(Duration.ofSeconds(30L));
        return requestFactory;
    }
}
