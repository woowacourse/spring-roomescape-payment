package roomescape.config;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.TossPaymentClient;
import roomescape.payment.TossPaymentConfirmErrorHandler;

@Configuration
public class RestClientConfiguration {

    @Bean
    public TossPaymentConfirmErrorHandler tossPaymentConfirmErrorHandler() {
        return new TossPaymentConfirmErrorHandler();
    }

    @Bean
    public TossPaymentClient tossPaymentClient(RestClient.Builder builder) {
        return new TossPaymentClient(builder.build(), tossPaymentConfirmErrorHandler());
    }

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return builder -> builder
                .requestFactory(getSimpleClientHttpRequestFactory());
    }

    private SimpleClientHttpRequestFactory getSimpleClientHttpRequestFactory() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(30000);
        return factory;
    }
}
