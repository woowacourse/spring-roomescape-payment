package roomescape.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import roomescape.service.payment.PaymentClient;

@Configuration
public class ClientConfig {
    private static final String BASE_URL = "https://api.tosspayments.com/v1/payments";
    private static final String BASIC_DELIMITER = ":";
    private static final String AUTH_HEADER_PREFIX = "Basic ";

    @Value("${payment.secret-key}")
    private String secretKey;

    @Bean
    public PaymentClient paymentClient(ObjectMapper objectMapper) {
        return new PaymentClient(objectMapper, createRestClient());
    }

    private RestClient createRestClient() {
        return RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, createAuthorization())
                .build();
    }

    private String createAuthorization() {
        return AUTH_HEADER_PREFIX + encodeToBase64(secretKey + BASIC_DELIMITER);
    }

    private String encodeToBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }
}
