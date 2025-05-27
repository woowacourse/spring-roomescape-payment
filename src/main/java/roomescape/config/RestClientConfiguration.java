package roomescape.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentConfirmClientException;
import roomescape.exception.PaymentConfirmServerException;

@Configuration
public class RestClientConfiguration {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (req, res) -> {
                    JsonNode root = objectMapper.readTree(res.getBody());
                    String message = root.path("message").asText();
                    throw new PaymentConfirmClientException(message);
                })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (req, res) -> {
                    JsonNode root = objectMapper.readTree(res.getBody());
                    String message = root.path("message").asText();
                    throw new PaymentConfirmServerException(message);
                })
                .build();
    }

}
