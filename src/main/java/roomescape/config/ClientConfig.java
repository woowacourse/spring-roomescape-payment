package roomescape.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentErrorHandler;
import roomescape.infra.PaymentClient;
import roomescape.infra.TossPaymentClient;

@Configuration
public class ClientConfig {

    @Value("${toss.widget.secretKey}")
    private String widgetSecretKey;

    @Bean
    public PaymentErrorHandler paymentErrorHandler(ObjectMapper objectMapper) {
        return new PaymentErrorHandler(objectMapper);
    }

    @Bean
    public PaymentClient PaymentRestClient(PaymentErrorHandler paymentErrorHandler) {
        RestClient restClient = RestClient.builder()
                .requestFactory(clientHttpRequestFactory())
                .baseUrl("https://api.tosspayments.com/v1/payments/confirm")
                .defaultStatusHandler(paymentErrorHandler)
                .build();
        
        return new TossPaymentClient(restClient, widgetSecretKey);
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(3L))
                .withReadTimeout(Duration.ofSeconds(3L));

        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, settings);
    }
}
