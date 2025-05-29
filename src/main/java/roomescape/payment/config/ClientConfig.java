package roomescape.payment.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.payment.resolver.PaymentClient;

@Configuration
public class ClientConfig {

    @Value("${payment.secret.key}")
    private String secretKey;

    @Bean
    public PaymentClient getPaymentResolver() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        return new PaymentClient(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com")
                        .defaultHeader("Content-Type", "application/json")
                        .defaultHeader("Authorization", authorizations)
                        .build()
        );
    }
}
