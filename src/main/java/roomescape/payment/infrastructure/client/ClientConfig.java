package roomescape.payment.infrastructure.client;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Value("${payment-api.secret-key}")
    private String secretKey;

    @Bean
    public PaymentRestClient paymentRestClient() {
        String base64Auth = Base64.getEncoder()
                .encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));

        return new PaymentRestClient(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com")
                        .defaultHeader("Authorization", "Basic " + base64Auth)
                        .build()
        );
    }
}
