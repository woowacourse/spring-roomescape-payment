package roomescape.payment.application;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.impl.TossConfirmException;
import roomescape.payment.application.dto.TossConfirmRequest;
import roomescape.payment.application.dto.TossConfirmResponse;
import roomescape.payment.application.dto.TossErrorResponse;

@Component
public class TossPaymentGatewayClient {

    private static final String BASE_URL = "https://api.tosspayments.com";
    private static final String CONFIRM_ENDPOINT = "/v1/payments/confirm";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPaymentGatewayClient(final ObjectMapper objectMapper) {
        this.restClient = RestClient.builder()
            .baseUrl(BASE_URL)
            .defaultHeader(AUTHORIZATION, encodeSecretKey("test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6"))
            .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .build();
        this.objectMapper = objectMapper;
    }

    private String encodeSecretKey(final String secretKey) {
        String raw = secretKey + ":";
        String encoded = Base64.getEncoder()
            .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }

    public TossConfirmResponse processPaymentConfirm(final TossConfirmRequest request) {
        return restClient.post()
            .uri(CONFIRM_ENDPOINT)
            .accept(APPLICATION_JSON)
            .body(request)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                TossErrorResponse error = deserializeError(res.getBody());
                throw new TossConfirmException(HttpStatus.BAD_REQUEST, error.code(), error.message());
            })
            .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                TossErrorResponse error = deserializeError(res.getBody());
                throw new TossConfirmException(HttpStatus.INTERNAL_SERVER_ERROR, error.code(), error.message());
            })
            .body(TossConfirmResponse.class);
    }

    private TossErrorResponse deserializeError(final InputStream bodyStream) {
        try {
            String body = StreamUtils.copyToString(bodyStream, StandardCharsets.UTF_8);
            return objectMapper.readValue(body, TossErrorResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing Toss error response", e);
        }
    }
}
