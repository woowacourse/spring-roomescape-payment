package roomescape.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentConfirmClientException;
import roomescape.exception.PaymentConfirmServerException;
import roomescape.infrastructure.TossErrorResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.Set;

@Configuration
public class HttpClientConfiguration {

    private static final String BASE_URL = "https://api.tosspayments.com";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_CODE = "code";
    private static final String BASIC = "Basic ";

    @Value("${toss.payment.confirm.connect-timeout}")
    private int connectTimeoutMillis;
    @Value("${toss.payment.confirm.read-timeout}")
    private int readTimeoutMillis;
    @Value("${toss.payment.confirm.secretKey}")
    private String secretKey;

    private static final Set<String> INVISIBLE_CLIENT_ERROR_CODE = Set.of(
            "INVALID_API_KEY",
            "INVALID_AUTHORIZE_AUTH",
            "UNAPPROVED_ORDER_ID",
            "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT"
    );

    private final ObjectMapper objectMapper;

    public HttpClientConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public RestClient.Builder builder() {
        return RestClient.builder();
    }

    @Bean(value = "tossRestClient")
    public RestClient tossRestClient() {
        return builder().requestFactory(getRequestFactory())
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, buildBasicAuthHeader())
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, this::handleClientError)
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, this::handleServerError)
                .build();
    }

    private HttpComponentsClientHttpRequestFactory getRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(connectTimeoutMillis));
        factory.setReadTimeout(Duration.ofMillis(readTimeoutMillis));
        return factory;
    }

    private String buildBasicAuthHeader() {
        return BASIC + Base64.getEncoder().encodeToString(secretKey.getBytes());
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
