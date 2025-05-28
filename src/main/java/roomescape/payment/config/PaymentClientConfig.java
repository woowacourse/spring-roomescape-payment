package roomescape.payment.config;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PaymentClientConfig {

    // TODO: 배포시 환경 변수로 변경하기
    private final static String tossSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6a";

    @Bean
    public RestClient tossPaymentClient() {
        String base64EncodedKey = Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizationHeader = "Basic " + base64EncodedKey;

        return RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments/confirm")
                .defaultHeader("Authorization", authorizationHeader)
                .defaultHeader("Content-Type", APPLICATION_JSON_VALUE)
                .build();
    }
}
