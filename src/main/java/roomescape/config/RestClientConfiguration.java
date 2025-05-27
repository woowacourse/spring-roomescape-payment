package roomescape.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentConfirmClientException;
import roomescape.exception.PaymentConfirmServerException;

@Configuration
public class RestClientConfiguration {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Set<String> INVISIBLE_CLIENT_ERROR_CODE = Set.of(
            "INVALID_API_KEY",
            "INVALID_AUTHORIZE_AUTH",
            "UNAPPROVED_ORDER_ID",
            "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT"
    );

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (req, res) -> {
                    JsonNode root = objectMapper.readTree(res.getBody());
                    String message = root.path("message").asText();
                    String code = root.path("code").asText();
                    if (INVISIBLE_CLIENT_ERROR_CODE.contains(code)) {
                        throw new PaymentConfirmServerException(message);
                    }
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
