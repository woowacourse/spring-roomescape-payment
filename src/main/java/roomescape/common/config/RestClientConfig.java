package roomescape.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.infrastructure.payment.PaymentClientProperties;
import roomescape.infrastructure.payment.toss.TossRestClientProperties;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder tossClientBuilder(TossRestClientProperties properties) {
        RestClient.Builder builder = RestClient.builder();
        return builder.requestFactory(createRequestFactory(properties)).baseUrl(properties.getBaseUrl());
    }

    private ClientHttpRequestFactory createRequestFactory(PaymentClientProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getConnectTimeout());
        requestFactory.setReadTimeout(properties.getReadTimeout());

        return requestFactory;
    }

}
