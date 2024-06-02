package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.api.TossPaymentClient;
import roomescape.service.PaymentClient;

import java.time.Duration;

@Configuration
public class PaymentConfig {

    @Bean
    public PaymentClient getTossPaymentClient(@Value("${security.api.toss.secret-key}") String widgetSecretKey,
                                              @Value("${api.toss.url}") String url,
                                              @Value("${api.connectTimeoutSecond}") int connectTimeoutSecond,
                                              @Value("${api.readTimeoutSecond}") int readTimeoutSecond) {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(Duration.ofSeconds(connectTimeoutSecond));
        simpleClientHttpRequestFactory.setReadTimeout(Duration.ofSeconds(readTimeoutSecond));

        RestClient restClient = RestClient.builder()
                .baseUrl(url)
                .requestFactory(simpleClientHttpRequestFactory)
                .build();
        return new TossPaymentClient(restClient, widgetSecretKey);
    }
}
