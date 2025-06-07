package roomescape.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.TossPaymentClient;
import roomescape.payment.TossPaymentConfirmErrorHandler;
import roomescape.payment.TossPaymentProperties;

@EnableConfigurationProperties(TossPaymentProperties.class)
@Configuration
public class RestClientConfiguration {

    private final TossPaymentProperties tossPaymentProperties;

    public RestClientConfiguration(TossPaymentProperties tossPaymentProperties) {
        this.tossPaymentProperties = tossPaymentProperties;
    }

    @Bean
    public TossPaymentConfirmErrorHandler tossPaymentConfirmErrorHandler() {
        return new TossPaymentConfirmErrorHandler();
    }

    @Bean
    public TossPaymentClient tossPaymentClient(RestClient.Builder builder) {
        return new TossPaymentClient(builder.build(), tossPaymentConfirmErrorHandler(), tossPaymentProperties);
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
