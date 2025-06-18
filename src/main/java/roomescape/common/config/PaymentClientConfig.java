package roomescape.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestClient;
import roomescape.common.properties.PaymentClientProperties;
import roomescape.payment.application.dto.TossPaymentMapper;
import roomescape.payment.exception.handler.PaymentExceptionHandler;

@Configuration
@EnableRetry
@EnableConfigurationProperties(PaymentClientProperties.class)
public class PaymentClientConfig {

    private final PaymentClientProperties paymentClientProperties;

    public PaymentClientConfig(final PaymentClientProperties paymentClientProperties) {
        this.paymentClientProperties = paymentClientProperties;
    }

    @Bean
    public RestClient restClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder
                .build();
    }

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return (restClientBuilder) -> {
            restClientBuilder.requestFactory(clientHttpRequestFactory())
                    .baseUrl(paymentClientProperties.getBaseUrl());
        };
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(30000);
        return factory;
    }

    @Bean
    public PaymentExceptionHandler paymentApproveExceptionHandler(ObjectMapper objectMapper) {
        return new PaymentExceptionHandler(objectMapper);
    }

    @Bean
    public TossPaymentMapper tossPaymentMapper() {
        return new TossPaymentMapper();
    }
}
