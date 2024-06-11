package roomescape.infra.payment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(PaymentClientProperties.class)
public class PaymentClientConfig {

    private final PaymentClientProperties paymentClientProperties;

    public PaymentClientConfig(PaymentClientProperties paymentClientProperties) {
        this.paymentClientProperties = paymentClientProperties;
    }

    @Bean
    public RestClient tossPaymentsRestClient() {
        PaymentClientProviderProperties properties = paymentClientProperties.getProvider("toss");

        RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(properties.connectionTimeoutInSeconds())
                .setReadTimeout(properties.readTimeoutInSeconds())
                .build();

        return RestClient.builder(restTemplate)
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, encodeSecretKey(properties.secretKey()))
                .build();
    }

    private String encodeSecretKey(String secretKey) {
        return "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }
}
