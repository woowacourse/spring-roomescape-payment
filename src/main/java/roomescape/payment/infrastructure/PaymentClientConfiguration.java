package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Base64;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Configuration
@EnableConfigurationProperties(PaymentGatewayProperties.class)
public class PaymentClientConfiguration {
    private final PaymentGatewayProperties paymentProperties;

    public PaymentClientConfiguration(PaymentGatewayProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
    }

    @Bean
    public RestClient.Builder tossPaymentsClientBuilder() {
        PaymentGatewayProperty property = paymentProperties.get("toss");
        return RestClient.builder()
                .baseUrl(property.baseUrl())
                .defaultHeader(AUTHORIZATION, encodeSecretKey(property.secretKey()))
                .requestFactory(clientHttpRequestFactory(property.readTimeout()));
    }

    private ClientHttpRequestFactory clientHttpRequestFactory(long readTimeout) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withReadTimeout(Duration.ofSeconds(readTimeout));
        return ClientHttpRequestFactories.get(settings);
    }

    private String encodeSecretKey(String secretKey) {
        return "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
    }
}
