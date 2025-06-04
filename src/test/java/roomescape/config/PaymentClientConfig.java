package roomescape.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import roomescape.client.TossErrorResponse;
import roomescape.client.TossPaymentClient;
import roomescape.exception.PaymentConfirmClientException;
import roomescape.exception.PaymentConfirmServerException;

import java.io.IOException;
import java.util.Base64;
import java.util.Set;

@TestConfiguration
public class PaymentClientConfig {

    public static final String BASIC = "Basic ";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_CODE = "code";

    private static final Set<String> INVISIBLE_CLIENT_ERROR_CODE = Set.of(
            "INVALID_API_KEY",
            "INVALID_AUTHORIZE_AUTH",
            "UNAPPROVED_ORDER_ID",
            "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT"
    );

    @Value("${security.toss.payment.secret-key}")
    private String secretKey;

    private final ObjectMapper objectMapper;


    public PaymentClientConfig(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public TossPaymentClient tossPaymentClient(RestClient.Builder builder) {
        return new TossPaymentClient(builder
                .baseUrl("https://api.tosspayments.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, BASIC + encodeSecretKey())
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, this::handleClientError)
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, this::handleServerError)
                .build());
    }

    private String encodeSecretKey() {
        return Base64.getEncoder().encodeToString("test-secret".getBytes());
    }

    private void handleClientError(HttpRequest request, ClientHttpResponse response) throws IOException {
        TossErrorResponse errorResponse = parseErrorResponse(response);
        if (INVISIBLE_CLIENT_ERROR_CODE.contains(errorResponse.code())) {
            throw new PaymentConfirmServerException(errorResponse);
        }
        throw new PaymentConfirmClientException(errorResponse);
    }

    private void handleServerError(HttpRequest request, ClientHttpResponse response) throws IOException {
        throw new PaymentConfirmServerException(parseErrorResponse(response));
    }

    private TossErrorResponse parseErrorResponse(ClientHttpResponse response) throws IOException {
        JsonNode root = objectMapper.readTree(response.getBody());
        String message = root.path(KEY_MESSAGE).asText();
        String code = root.path(KEY_CODE).asText();
        return new TossErrorResponse(response.getStatusCode(), code, message);
    }
}
