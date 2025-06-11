package roomescape.payment.config;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class PaymentClientConfig {

    @Value("${toss.confirm.secret-key}")
    private String tossSecretKey;

    @Bean
    public RestClient tossPaymentClient() {
        String base64EncodedKey = Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizationHeader = "Basic " + base64EncodedKey;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(30));

        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl("https://api.tosspayments.com/v1/payments/confirm")
                .defaultHeader("Authorization", authorizationHeader)
                .defaultHeader("Content-Type", APPLICATION_JSON_VALUE)
                .build();
    }
}
