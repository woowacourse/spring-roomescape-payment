package roomescape.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.client.TossPaymentsClient;

@Configuration
public class ClientConfig {

    private final String secretKey;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public ClientConfig(@Value("${toss.payments.secret-key}") final String secretKey,
                        @Value("${toss.payments.url}") final String baseUrl,
                        final ObjectMapper objectMapper) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;
    }

    @Bean
    public TossPaymentsClient tossRestClient() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(30000);
        return new TossPaymentsClient(
                RestClient.builder()
                        .baseUrl(baseUrl)
                        .requestFactory(requestFactory)
                        .defaultHeader("Authorization", getBasicAuthorizationValue())
                        .build(),
                objectMapper
        );
    }

    private String getBasicAuthorizationValue() {
        return "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
    }
}
