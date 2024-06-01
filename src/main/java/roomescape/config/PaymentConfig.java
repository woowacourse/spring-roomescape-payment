package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.api.TossPaymentClient;
import roomescape.service.PaymentClient;

@Configuration
public class PaymentConfig {

    @Bean
    public PaymentClient getTossPaymentClient(@Value("${security.api.toss.secret-key}") String widgetSecretKey,
                                              @Value("${security.api.toss.url}") String url) {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(3);
        simpleClientHttpRequestFactory.setReadTimeout(30);

        RestClient restClient = RestClient.builder()
                .baseUrl(url)
                .requestFactory(simpleClientHttpRequestFactory)
                .build();
        return new TossPaymentClient(restClient, widgetSecretKey);
    }
}
