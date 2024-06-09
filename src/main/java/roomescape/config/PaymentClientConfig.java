package roomescape.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.config.PaymentGatewayProperties.Gateway;
import roomescape.infrastructure.payment.LoggingInterceptor;
import roomescape.infrastructure.payment.TimeoutInterceptor;
import roomescape.infrastructure.payment.TossPaymentClient;
import roomescape.infrastructure.payment.TossPaymentClientErrorHandler;
import roomescape.util.Base64Encoder;
import roomescape.util.BasicAuthGenerator;

@Configuration
@EnableConfigurationProperties(PaymentGatewayProperties.class)
public class PaymentClientConfig {

    private final PaymentGatewayProperties properties;

    public PaymentClientConfig(PaymentGatewayProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .requestInterceptor(new TimeoutInterceptor())
                .requestInterceptor(new LoggingInterceptor());
    }

    @Bean
    public TossPaymentClient tossPaymentClient(ObjectMapper objectMapper) {
        Gateway toss = properties.getGateway("toss");
        String encodedSecretKey = Base64Encoder.encode(toss.getSecretKey() + ":");
        RestClient restClient = restClientBuilder()
                .requestFactory(getRequestFactory(toss))
                .defaultStatusHandler(new TossPaymentClientErrorHandler(objectMapper))
                .defaultHeader(HttpHeaders.AUTHORIZATION, BasicAuthGenerator.generate(encodedSecretKey))
                .baseUrl(toss.getUri())
                .build();
        return new TossPaymentClient(restClient);
    }

    private ClientHttpRequestFactory getRequestFactory(Gateway gateway) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(gateway.getConnectTimeout())
                .withReadTimeout(gateway.getReadTimeout());
        return ClientHttpRequestFactories.get(settings);
    }
}
