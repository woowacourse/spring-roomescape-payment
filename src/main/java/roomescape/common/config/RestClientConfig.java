package roomescape.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.infrastructure.payment.PaymentClientProperties;
import roomescape.common.interceptor.RestClientInterceptor;
import roomescape.infrastructure.payment.toss.TossRestClientProperties;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder tossClientBuilder(TossRestClientProperties properties,
                                                RestClientInterceptor interceptor) {
        RestClient.Builder builder = RestClient.builder();
        return builder
                .baseUrl(properties.getBaseUrl())
                .requestInterceptor(interceptor)
                .requestFactory(createRequestFactory(properties));
    }

    @Bean
    public RestClientInterceptor restClientInterceptor() {
        return new RestClientInterceptor();
    }

    private ClientHttpRequestFactory createRequestFactory(PaymentClientProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getConnectTimeout());
        requestFactory.setReadTimeout(properties.getReadTimeout());

        return requestFactory;
    }

}
