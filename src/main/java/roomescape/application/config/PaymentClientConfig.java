package roomescape.application.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import roomescape.application.payment.PaymentErrorHandler;

@Configuration
public class PaymentClientConfig {
    private final String paymentUrl;

    public PaymentClientConfig(@Value("${payment.url}") String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    @Bean
    public ResponseErrorHandler errorHandler() {
        return new PaymentErrorHandler();
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        RestTemplate template = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(3L))
                .setReadTimeout(Duration.ofSeconds(30L))
                .build();
        return RestClient.builder(template)
                .baseUrl(paymentUrl);
    }
}
