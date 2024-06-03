package roomescape.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;

@Configuration
@Profile("!local")
public class ClientConfiguration {

    @Value("${security.payment.secret-key}")
    private String WIDGET_SECRET_KEY;

    @Bean
    public RestClient tossPaymentRestClient() {
        return RestClient.builder()
            .baseUrl("https://api.tosspayments.com")
            .defaultHeader("Authorization", getAuthorization())
            .build();
    }

    private String getAuthorization() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((WIDGET_SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
