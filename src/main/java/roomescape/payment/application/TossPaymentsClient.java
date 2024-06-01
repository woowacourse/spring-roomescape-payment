package roomescape.payment.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.global.exception.ViolationException;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.TossPaymentsErrorResponse;
import roomescape.payment.exception.PaymentServerException;

import java.io.IOException;
import java.time.Duration;
import java.util.Base64;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class TossPaymentsClient {
    private static final int CONNECTION_TIMEOUT_SECOND = 3;
    private static final int READ_TIMEOUT_SECOND = 30;

    private final RestClient restClient;
    private final String confirmApiPath;
    private final ObjectMapper objectMapper;

    public TossPaymentsClient(@Value("${pg.toss.secret-key}") String secretKey,
                              @Value("${pg.toss.base-url}") String baseUrl,
                              @Value("${pg.toss.confirm-api-path}") String confirmApiPath,
                              ObjectMapper objectMapper) {
        this.restClient = createRestClient(secretKey, baseUrl);
        this.confirmApiPath = confirmApiPath;
        this.objectMapper = objectMapper;
    }

    private RestClient createRestClient(String secretKey, String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(AUTHORIZATION, encodeSecretKey(secretKey))
                .requestFactory(getRequestFactory())
                .build();
    }

    private String encodeSecretKey(String secretKey) {
        return "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
    }

    private ClientHttpRequestFactory getRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT_SECOND))
                .withReadTimeout(Duration.ofSeconds(READ_TIMEOUT_SECOND));

        return ClientHttpRequestFactories.get(settings);
    }

    public void confirm(PaymentConfirmRequest request) {
        restClient.post()
                .uri(confirmApiPath)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ViolationException(extractErrorMessage(res));
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new PaymentServerException(extractErrorMessage(res));
                })
                .toBodilessEntity();
    }

    private String extractErrorMessage(ClientHttpResponse response) throws IOException {
        return objectMapper.readValue(response.getBody(), TossPaymentsErrorResponse.class).message();
    }
}
