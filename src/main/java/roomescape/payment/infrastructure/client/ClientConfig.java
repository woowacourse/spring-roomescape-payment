package roomescape.payment.infrastructure.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Configuration
public class ClientConfig {

    private static final String URL = "https://api.tosspayments.com";
    private static final String HEADER_NAME = "Authorization";
    private static final String HEADER_VALUE = "Basic ";

    @Value("${payment-api.secret-key}")
    private String secretKey;

    private final ObjectMapper objectMapper;

    @Bean
    public TossPaymentRestClient paymentRestClient() {
        final String base64Auth = getEncodedKey();
        return new TossPaymentRestClient(
                RestClient.builder()
                        .baseUrl(URL)
                        .defaultHeader(HEADER_NAME, HEADER_VALUE + base64Auth)
                        .build(),
                objectMapper
        );
    }

    private String getEncodedKey() {
        return Base64.getEncoder()
                .encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
